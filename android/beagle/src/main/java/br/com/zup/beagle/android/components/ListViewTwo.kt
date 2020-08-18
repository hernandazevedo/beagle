/*
 * Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.zup.beagle.android.components

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.zup.beagle.android.action.Action
import br.com.zup.beagle.android.action.SetContext
import br.com.zup.beagle.android.context.Bind
import br.com.zup.beagle.android.context.ContextComponent
import br.com.zup.beagle.android.context.ContextData
import br.com.zup.beagle.android.context.expressionOf
import br.com.zup.beagle.android.context.normalize
import br.com.zup.beagle.android.utils.generateViewModelInstance
import br.com.zup.beagle.android.utils.getContextBinding
import br.com.zup.beagle.android.utils.observeBindChanges
import br.com.zup.beagle.android.utils.setContextData
import br.com.zup.beagle.android.view.ViewFactory
import br.com.zup.beagle.android.view.custom.BeagleFlexView
import br.com.zup.beagle.android.view.viewmodel.ScreenContextViewModel
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.android.widget.WidgetView
import br.com.zup.beagle.annotation.RegisterWidget
import br.com.zup.beagle.core.ServerDrivenComponent
import br.com.zup.beagle.core.Style
import br.com.zup.beagle.widget.core.Flex
import br.com.zup.beagle.widget.core.FlexDirection.COLUMN
import br.com.zup.beagle.widget.core.FlexDirection.ROW
import br.com.zup.beagle.widget.core.ListDirection

@RegisterWidget
internal data class ListViewTwo(
    override val context: ContextData? = null,
    val onInit: List<Action>? = null,
    val dataSource: Bind<List<Any>>,
    val direction: ListDirection,
    val template: ServerDrivenComponent,
    val onScrollEnd: List<Action>? = null,
    val scrollThreshold: Int? = null,
    val useParentScroll: Boolean = false,
    val iteratorName: String? = null
) : WidgetView(), ContextComponent {

    @Transient
    private val viewFactory: ViewFactory = ViewFactory()

    @Transient
    private lateinit var contextAdapter: ListViewContextAdapter2

    @Transient
    private var list: List<Any>? = null

    override fun buildView(rootView: RootView): View {
        val recyclerView = viewFactory.makeRecyclerView(rootView.getContext())

        val orientation = toRecyclerViewOrientation()
        contextAdapter = ListViewContextAdapter2(template, iteratorName, viewFactory, orientation, rootView)
//        contextAdapter.setHasStableIds(true)
        recyclerView.apply {
            setHasFixedSize(true)
            adapter = contextAdapter
            layoutManager = LinearLayoutManager(context, orientation, false)
            isNestedScrollingEnabled = useParentScroll
        }
        configDataSourceObserver(rootView, recyclerView)
        configRecyclerViewScrollListener(recyclerView, rootView)

        onInit?.forEach { action ->
            action.execute(rootView, recyclerView)
        }

        return recyclerView
    }

    private fun toRecyclerViewOrientation() = if (direction == ListDirection.VERTICAL) {
        RecyclerView.VERTICAL
    } else {
        RecyclerView.HORIZONTAL
    }

    private fun configDataSourceObserver(rootView: RootView, recyclerView: RecyclerView) {
        observeBindChanges(rootView, recyclerView, dataSource) { value ->
            if (value != list) {
                if (value.isNullOrEmpty()) {
                    contextAdapter.clearList()
                } else {
                    contextAdapter.setList(value)
                }
                list = value
            }
        }
    }

    private fun configRecyclerViewScrollListener(recyclerView: RecyclerView, rootView: RootView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (needToExecuteOnScrollEnd(recyclerView)) {
                    onScrollEnd?.forEach { action ->
                        action.execute(rootView, recyclerView)
                    }
                }
            }
        })
    }

    private fun needToExecuteOnScrollEnd(recyclerView: RecyclerView): Boolean {
        val scrolledPercent = calculateScrolledPercent(recyclerView)
        scrollThreshold?.let {
            return scrolledPercent >= scrollThreshold
        }
        return scrolledPercent == 100f
    }

    private fun calculateScrolledPercent(recyclerView: RecyclerView): Float {
        val layoutManager = LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
        var scrolled = 0f
        layoutManager?.let {
            val totalItemCount = it.itemCount.toFloat()
            val lastVisible = it.findLastVisibleItemPosition().toFloat()
            scrolled = (lastVisible / totalItemCount) * 100
        }
        return scrolled
    }
}

internal class ListViewContextAdapter2(
    private val template: ServerDrivenComponent,
    private val iteratorName: String? = null,
    private val viewFactory: ViewFactory,
    private val orientation: Int,
    private val rootView: RootView,
    private var listItems: ArrayList<Any> = ArrayList()
) : RecyclerView.Adapter<ContextViewHolderTwo>() {

    private val viewModel = rootView.generateViewModelInstance<ScreenContextViewModel>()

//    override fun getItemViewType(position: Int) = position

//    override fun getItemId(position: Int) = position.toLong()

    override fun onViewRecycled(holder: ContextViewHolderTwo) {
        super.onViewRecycled(holder)
//        Log.i("LIST", "onViewRecycled")
    }

    override fun onViewDetachedFromWindow(holder: ContextViewHolderTwo) {
        super.onViewDetachedFromWindow(holder)
//        Log.i("LIST", "onViewDetachedFromWindow")
        viewModel.clearContexts(holder.itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ContextViewHolderTwo {
//        Log.i("LIST", "onCreateViewHolder")
        val view = viewFactory.makeBeagleFlexView(
            rootView.getContext(),
            Style(flex = Flex(flexDirection = flexDirection()))
        ).apply {
            id = viewModel.generateNewViewId()
            layoutParams = LayoutParams(layoutParamWidth(), layoutParamHeight())
            addServerDrivenComponent(template, this@ListViewContextAdapter2.rootView)
//            setContextData(ContextData(id = getContextDataId(), value = ""))
        }
//        viewModel.addContext(view, ContextData(id = getContextDataId(), value = ""))
//        viewModel.linkBindingToContext()
        return ContextViewHolderTwo(view)
    }

    private fun layoutParamWidth() = if (isOrientationVertical()) MATCH_PARENT else WRAP_CONTENT

    private fun layoutParamHeight() = if (isOrientationVertical()) WRAP_CONTENT else MATCH_PARENT

    private fun flexDirection() = if (isOrientationVertical()) COLUMN else ROW

    private fun isOrientationVertical() = (orientation == RecyclerView.VERTICAL)

    private fun getContextDataId() = iteratorName ?: "item"

    override fun onBindViewHolder(holder: ContextViewHolderTwo, position: Int) {
//        Log.i("LIST", "onBindViewHolder position $position")

//        holder.itemView.setContextData(ContextData(id = getContextDataId(), value = listItems[position]))

        viewModel.addContext(holder.itemView, ContextData(id = getContextDataId(), value = listItems[position]))
        viewModel.linkBindingToContextAndEvaluateThem(holder.itemView)

//        viewModel.addImplicitContext(
//            ContextData(id = getContextDataId(), value = listItems[position]).normalize(),
//            holder.itemView,
//            emptyList()
//        )
    }

    override fun onViewAttachedToWindow(holder: ContextViewHolderTwo) {
//        Log.i("LIST", "onViewAttachedToWindow")
        super.onViewAttachedToWindow(holder)


//        viewModel.evaluateExpressionForImplicitContext(
//            holder.itemView,
//            bind = expressionOf<Any>("@{${getContextDataId()}}")
//        )

    }

    fun setList(list: List<Any>) {
        listItems = ArrayList(list)
        notifyDataSetChanged()
    }

    fun clearList() {
        val initialSize = listItems.size
        listItems.clear()
        notifyItemRangeRemoved(0, initialSize)
    }

    override fun getItemCount(): Int = listItems.size
}

internal class ContextViewHolderTwo(itemView: View) : RecyclerView.ViewHolder(itemView)
