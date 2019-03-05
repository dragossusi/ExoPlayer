/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.ui

import android.content.Context
import android.util.AttributeSet
import com.google.android.exoplayer2.util.RepeatModeUtil


@Deprecated("Use {@link PlayerControlView}. ")
class PlaybackControlView : PlayerControlView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, playbackAttrs: AttributeSet)
            : super(context, attrs, defStyleAttr, playbackAttrs)

    @Deprecated("Use {@link com.google.android.exoplayer2.ControlDispatcher}. ")
    interface ControlDispatcher : com.google.android.exoplayer2.ControlDispatcher


    @Deprecated("Use {@link com.google.android.exoplayer2.ui.PlayerControlView.VisibilityListener}.")
    interface VisibilityListener : com.google.android.exoplayer2.ui.PlayerControlView.VisibilityListener

    @Deprecated("")
    private class DefaultControlDispatcher : com.google.android.exoplayer2.DefaultControlDispatcher(), ControlDispatcher

    companion object {

        @Deprecated("Use {@link com.google.android.exoplayer2.DefaultControlDispatcher}. ", ReplaceWith("DefaultControlDispatcher","com.google.android.exoplayer2"))
        val DEFAULT_CONTROL_DISPATCHER: ControlDispatcher = DefaultControlDispatcher()

        /** The default fast forward increment, in milliseconds.  */
        const val DEFAULT_FAST_FORWARD_MS = PlayerControlView.DEFAULT_FAST_FORWARD_MS
        /** The default rewind increment, in milliseconds.  */
        const val DEFAULT_REWIND_MS = PlayerControlView.DEFAULT_REWIND_MS
        /** The default show timeout, in milliseconds.  */
        const val DEFAULT_SHOW_TIMEOUT_MS = PlayerControlView.DEFAULT_SHOW_TIMEOUT_MS
        /** The default repeat toggle modes.  */
        @RepeatModeUtil.RepeatToggleModes
        val DEFAULT_REPEAT_TOGGLE_MODES = PlayerControlView.DEFAULT_REPEAT_TOGGLE_MODES

        /** The maximum number of windows that can be shown in a multi-window time bar.  */
        val MAX_WINDOWS_FOR_MULTI_WINDOW_TIME_BAR = PlayerControlView.MAX_WINDOWS_FOR_MULTI_WINDOW_TIME_BAR
    }

}
