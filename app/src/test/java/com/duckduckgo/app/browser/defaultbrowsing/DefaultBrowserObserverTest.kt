/*
 * Copyright (c) 2019 DuckDuckGo
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

package com.duckduckgo.app.browser.defaultbrowsing

import androidx.lifecycle.LifecycleOwner
import com.duckduckgo.app.global.install.AppInstallStore
import com.duckduckgo.app.pixels.AppPixelName
import com.duckduckgo.app.statistics.api.featureusage.FeatureSegmentsManager
import com.duckduckgo.app.statistics.pixels.Pixel
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*

class DefaultBrowserObserverTest {

    private lateinit var testee: DefaultBrowserObserver

    @Mock
    private lateinit var mockDefaultBrowserDetector: DefaultBrowserDetector

    @Mock
    private lateinit var mockAppInstallStore: AppInstallStore

    @Mock
    private lateinit var mockPixel: Pixel

    @Mock
    private lateinit var mockFeatureSegmentsManager: FeatureSegmentsManager

    private val mockOwner: LifecycleOwner = mock()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        testee = DefaultBrowserObserver(mockDefaultBrowserDetector, mockAppInstallStore, mockPixel, mockFeatureSegmentsManager)
    }

    @Test
    fun whenDDGIsDefaultBrowserIfItWasNotBeforeThenFireSetPixel() {
        whenever(mockDefaultBrowserDetector.isDefaultBrowser()).thenReturn(true)
        whenever(mockAppInstallStore.defaultBrowser).thenReturn(false)
        val params = mapOf(
            Pixel.PixelParameter.DEFAULT_BROWSER_SET_FROM_ONBOARDING to false.toString(),
        )

        testee.onResume(mockOwner)

        verify(mockPixel).fire(AppPixelName.DEFAULT_BROWSER_SET, params)
    }

    @Test
    fun whenDDGIsNotDefaultBrowserIfItWasNotBeforeThenDoNotFireSetPixel() {
        whenever(mockDefaultBrowserDetector.isDefaultBrowser()).thenReturn(false)
        whenever(mockAppInstallStore.defaultBrowser).thenReturn(false)

        testee.onResume(mockOwner)

        verify(mockPixel, never()).fire(eq(AppPixelName.DEFAULT_BROWSER_SET), any(), any())
    }

    @Test
    fun whenDDGIsDefaultBrowserIfItWasBeforeThenDoNotFireSetPixel() {
        whenever(mockDefaultBrowserDetector.isDefaultBrowser()).thenReturn(true)
        whenever(mockAppInstallStore.defaultBrowser).thenReturn(true)

        testee.onResume(mockOwner)

        verify(mockPixel, never()).fire(eq(AppPixelName.DEFAULT_BROWSER_SET), any(), any())
    }

    @Test
    fun whenDDGIsNotDefaultBrowserIfItWasBeforeThenFireUnsetPixel() {
        whenever(mockDefaultBrowserDetector.isDefaultBrowser()).thenReturn(false)
        whenever(mockAppInstallStore.defaultBrowser).thenReturn(true)

        testee.onResume(mockOwner)

        verify(mockPixel).fire(AppPixelName.DEFAULT_BROWSER_UNSET)
    }
}
