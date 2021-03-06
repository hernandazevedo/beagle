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

package br.com.zup.beagle.android.context.operations.common.read.strategy

import br.com.zup.beagle.android.context.operations.common.ExtractValueTypes

internal class ArrayLogic(
    private val inputValue: String,
    private val extractValueTypes: ExtractValueTypes,
    private val delimiterEndIndex: Int
) : ReadLogic {

    override fun extractValue(): String {
        var startIndex = 1

        while (startIndex > 0) {
            if (inputValue[startIndex] != extractValueTypes.start) {
                startIndex--
            }
        }

        return inputValue.substring(startIndex, delimiterEndIndex + 1)
    }
}