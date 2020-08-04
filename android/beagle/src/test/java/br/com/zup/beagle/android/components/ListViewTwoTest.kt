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

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.zup.beagle.android.action.Action
import br.com.zup.beagle.android.extensions.once
import br.com.zup.beagle.android.view.ViewFactory
import br.com.zup.beagle.core.ServerDrivenComponent
import br.com.zup.beagle.widget.core.ListDirection
import io.mockk.*
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ListViewTwoTest : BaseComponentTest() {

    private val recyclerView: RecyclerView = mockk(relaxed = true)
    private val template: ServerDrivenComponent = mockk(relaxed = true)
    private val onInit: Action = mockk(relaxed = true)

    private val layoutManagerSlot = slot<LinearLayoutManager>()
    private val adapterSlot = slot<RecyclerView.Adapter<RecyclerView.ViewHolder>>()
    private val isNestedScrollingEnabledSlot = slot<Boolean>()

    private lateinit var listView: ListViewTwo

    override fun setUp() {
        super.setUp()
        every { anyConstructed<ViewFactory>().makeRecyclerView(rootView.getContext()) } returns recyclerView
        every { recyclerView.layoutManager = capture(layoutManagerSlot) } just Runs
        every { recyclerView.adapter = capture(adapterSlot) } just Runs
        every { recyclerView.isNestedScrollingEnabled = capture(isNestedScrollingEnabledSlot) } just Runs
    }

    @Test
    fun build_view_should_set_orientation_VERTICAL_when_direction_is_ListDirection_VERTICAL() {
        //given
        listView = ListViewTwo(
            direction = ListDirection.VERTICAL,
            template = template
        )
        //when
        listView.buildView(rootView)

        //then
        assertEquals(RecyclerView.VERTICAL, layoutManagerSlot.captured.orientation)
    }

    @Test
    fun build_view_should_set_orientation_VERTICAL_when_direction_is_ListDirection_HORIZONTAL() {
        //given
        listView = ListViewTwo(
            direction = ListDirection.HORIZONTAL,
            template = template
        )
        //when
        listView.buildView(rootView)

        //then
        assertEquals(RecyclerView.HORIZONTAL, layoutManagerSlot.captured.orientation)
    }

    @Test
    fun build_view_should_set_adapter() {
        //given
        listView = ListViewTwo(
            direction = ListDirection.HORIZONTAL,
            template = template
        )
        //when
        listView.buildView(rootView)

        //then
        assertTrue { adapterSlot.isCaptured }
    }

    @Test
    fun build_view_should_set_isNestedScrollingEnabled_false_when_useParentScroll_is_not_passed() {
        //given
        listView = ListViewTwo(
            direction = ListDirection.HORIZONTAL,
            template = template
        )
        //when
        listView.buildView(rootView)

        //then
        assertFalse { isNestedScrollingEnabledSlot.captured}
    }

    @Test
    fun build_view_should_set_isNestedScrollingEnabled_false_when_useParentScroll_is_false() {
        //given
        listView = ListViewTwo(
            direction = ListDirection.HORIZONTAL,
            template = template,
            useParentScroll = false
        )
        //when
        listView.buildView(rootView)

        //then
        assertFalse { isNestedScrollingEnabledSlot.captured }
    }

    @Test
    fun build_view_should_set_isNestedScrollingEnabled_true_when_useParentScroll_is_true() {
        //given

        listView = ListViewTwo(
            direction = ListDirection.HORIZONTAL,
            template = template,
            useParentScroll = true
        )
        //when
        listView.buildView(rootView)

        //then
        assertTrue { isNestedScrollingEnabledSlot.captured }
    }

    @Test
    fun build_view_should_execute_on_init_once() {
        //given

        listView = ListViewTwo(
            direction = ListDirection.HORIZONTAL,
            template = template,
            onInit = listOf(onInit)
        )
        every {
            onInit.execute(rootView, recyclerView)
        } just Runs
        //when
        listView.buildView(rootView)

        //then
        verify(exactly = once()) {
            onInit.execute(rootView, recyclerView)
        }
    }

}