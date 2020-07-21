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

package com.example.automated_tests.cucumber.steps

import androidx.test.rule.ActivityTestRule
import com.example.automated_tests.MainActivity
import com.example.automated_tests.cucumber.elements.*
import com.example.automated_tests.cucumber.robots.ScreenRobot
import com.example.automated_tests.utils.ActivityFinisher
import com.example.automated_tests.utils.TestUtils
import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.*
import org.junit.Rule

class TabViewScreenSteps {

    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before("@tabview")
    fun setup() {
        TestUtils.startActivity(activityTestRule, "http://10.0.2.2:8080/tabview")
    }

    @After("@tabview")
    fun tearDown() {
        ActivityFinisher.finishOpenActivities()
    }

    @Given("^that I'm on the tabview screen$")
    fun checkTabViewScreen() {
        ScreenRobot()
            .checkViewContainsText(MAIN_HEADER)
            .checkViewContainsText(TABVIEW_SCREEN_HEADER)
            .sleep(2)
    }

    @Then("^my tabview components should render their respective tabs attributes correctly$")
    fun checkTabViewRendersTabs() {
        ScreenRobot()
            .checkViewContainsText(TAB_1)
            .checkViewContainsText(TAB_1_TEXT)
            .swipeLeftOnView()
            .checkViewContainsText(TAB_2)
            .checkViewContainsText(TAB_2_TEXT)
            .swipeLeftOnView()
            .checkViewContainsText(TAB_3)
            .checkViewContainsText(TAB_3_TEXT)
            .swipeLeftOnView()
            .checkViewContainsText(TAB_4)
            .checkViewContainsText(TAB_4_TEXT)
            .swipeRightOnView()
            .swipeRightOnView()
            .swipeRightOnView()
    }

    @When("^I click on (.*)$")
    fun clickOnTab1(string1: String?) {
        ScreenRobot()
            .clickOnText(string1)
    }

    @Then("^my tab should render the text (.*) and (.*) correctly$")
    fun renderTextCorrectly(string1: String?, string2: String?) {
        ScreenRobot()
            .checkViewContainsText(string1)
            .checkViewContainsText(string2)
    }
}