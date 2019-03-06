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

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.os.Looper
import android.os.SystemClock
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.util.Assertions
import com.google.android.exoplayer2.util.RepeatModeUtil
import com.google.android.exoplayer2.util.Util
import java.util.*

/**
 * A view for controlling [Player] instances.
 *
 *
 * A PlayerControlView can be customized by setting attributes (or calling corresponding
 * methods), overriding the view's layout file or by specifying a custom view layout file, as
 * outlined below.
 *
 * <h3>Attributes</h3>
 *
 * The following attributes can be set on a PlayerControlView when used in a layout XML file:
 *
 *
 *  * **`show_timeout`** - The time between the last user interaction and the controls
 * being automatically hidden, in milliseconds. Use zero if the controls should not
 * automatically timeout.
 *
 *  * Corresponding method: [.setShowTimeoutMs]
 *  * Default: [.DEFAULT_SHOW_TIMEOUT_MS]
 *
 *  * **`rewind_increment`** - The duration of the rewind applied when the user taps the
 * rewind button, in milliseconds. Use zero to disable the rewind button.
 *
 *  * Corresponding method: [.setRewindIncrementMs]
 *  * Default: [.DEFAULT_REWIND_MS]
 *
 *  * **`fastforward_increment`** - Like `rewind_increment`, but for fast forward.
 *
 *  * Corresponding method: [.setFastForwardIncrementMs]
 *  * Default: [.DEFAULT_FAST_FORWARD_MS]
 *
 *  * **`repeat_toggle_modes`** - A flagged enumeration value specifying which repeat
 * mode toggle options are enabled. Valid values are: `none`, `one`, `all`,
 * or `one|all`.
 *
 *  * Corresponding method: [.setRepeatToggleModes]
 *  * Default: [PlayerControlView.DEFAULT_REPEAT_TOGGLE_MODES]
 *
 *  * **`show_shuffle_button`** - Whether the shuffle button is shown.
 *
 *  * Corresponding method: [.setShowShuffleButton]
 *  * Default: false
 *
 *  * **`controller_layout_id`** - Specifies the id of the layout to be inflated. See
 * below for more details.
 *
 *  * Corresponding method: None
 *  * Default: `R.layout.exo_player_control_view`
 *
 *
 *
 * <h3>Overriding the layout file</h3>
 *
 * To customize the layout of PlayerControlView throughout your app, or just for certain
 * configurations, you can define `exo_player_control_view.xml` layout files in your
 * application `res/layout*` directories. These layouts will override the one provided by the
 * ExoPlayer library, and will be inflated for use by PlayerControlView. The view identifies and
 * binds its children by looking for the following ids:
 *
 *
 *
 *
 *
 *  * **`exo_play`** - The play button.
 *
 *  * Type: [View]
 *
 *  * **`exo_pause`** - The pause button.
 *
 *  * Type: [View]
 *
 *  * **`exo_ffwd`** - The fast forward button.
 *
 *  * Type: [View]
 *
 *  * **`exo_rew`** - The rewind button.
 *
 *  * Type: [View]
 *
 *  * **`exo_prev`** - The previous track button.
 *
 *  * Type: [View]
 *
 *  * **`exo_next`** - The next track button.
 *
 *  * Type: [View]
 *
 *  * **`exo_repeat_toggle`** - The repeat toggle button.
 *
 *  * Type: [View]
 *
 *  * **`exo_shuffle`** - The shuffle button.
 *
 *  * Type: [View]
 *
 *  * **`exo_position`** - Text view displaying the current playback position.
 *
 *  * Type: [TextView]
 *
 *  * **`exo_duration`** - Text view displaying the current media duration.
 *
 *  * Type: [TextView]
 *
 *  * **`exo_progress`** - Time bar that's updated during playback and allows seeking.
 *
 *  * Type: [TimeBar]
 *
 *  * **`exo_quality_change`** - Quality change.
 *
 *  * Type: [ImageView]
 *
 *  * **`exo_fullscreen_btn`** - The fullscreen button.
 *
 *  * Type: [ImageView]
 *
 *
 *
 *
 * All child views are optional and so can be omitted if not required, however where defined they
 * must be of the expected type.
 *
 * <h3>Specifying a custom layout file</h3>
 *
 * Defining your own `exo_player_control_view.xml` is useful to customize the layout of
 * PlayerControlView throughout your application. It's also possible to customize the layout for a
 * single instance in a layout file. This is achieved by setting the `controller_layout_id`
 * attribute on a PlayerControlView. This will cause the specified layout to be inflated instead of
 * `exo_player_control_view.xml` for only the instance on which the attribute is set.
 */
open class PlayerControlView(
        context: Context, attrs: AttributeSet?, defStyleAttr: Int, playbackAttrs: AttributeSet?) : FrameLayout(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, attrs)

    private val componentListener: ComponentListener
    private val previousButton: View?
    private val nextButton: View?
    private val playButton: View?
    private val pauseButton: View?
    private val fastForwardButton: View?
    private val rewindButton: View?
    private val repeatToggleButton: ImageView?
    private val qualityChangeView: ImageView?
    private val fullscreenView: ImageView?
    private val shuffleButton: View?
    private val durationView: TextView?
    private val positionView: TextView?
    private val timeBar: TimeBar?
    private val formatBuilder: StringBuilder
    private val formatter: Formatter
    private val period: Timeline.Period
    private val window: Timeline.Window
    private val updateProgressAction: Runnable
    private val hideAction: Runnable


    private val repeatOffButtonDrawable: Drawable
    private val repeatOneButtonDrawable: Drawable
    private val repeatAllButtonDrawable: Drawable
    private val repeatOffButtonContentDescription: String
    private val repeatOneButtonContentDescription: String
    private val repeatAllButtonContentDescription: String

    /**
     * Returns the [Player] currently being controlled by this view, or null if no player is
     * set.
     */
    /**
     * Sets the [Player] to control.
     *
     * @param player The [Player] to control, or `null` to detach the current player. Only
     * players which are accessed on the main thread are supported (`player.getApplicationLooper() == Looper.getMainLooper()`).
     */
    var player: Player? = null
        set(player) {
            Assertions.checkState(Looper.myLooper() == Looper.getMainLooper())
            Assertions.checkArgument(
                    player == null || player.applicationLooper == Looper.getMainLooper())
            if (this.player === player) {
                return
            }
            if (this.player != null) {
                this.player!!.removeListener(componentListener)
            }
            field = player
            player?.addListener(componentListener)
            updateAll()
        }
    private var controlDispatcher: com.google.android.exoplayer2.ControlDispatcher? = null
    private var visibilityListener: VisibilityListener? = null
    private var playbackPreparer: PlaybackPreparer? = null

    private var _isAttachedToWindow: Boolean = false
    private var showMultiWindowTimeBar: Boolean = false
    private var multiWindowTimeBar: Boolean = false
    private var scrubbing: Boolean = false
    private var rewindMs: Int = 0
    private var fastForwardMs: Int = 0
    private var showTimeoutMs: Int = 0
    @RepeatModeUtil.RepeatToggleModes
    private var repeatToggleModes: Int = 0
    private var showShuffleButton: Boolean = false
    private var hideAtMs: Long = 0
    private var adGroupTimesMs: LongArray? = null
    private var playedAdGroups: BooleanArray? = null
    private var extraAdGroupTimesMs: LongArray? = null
    private var extraPlayedAdGroups: BooleanArray? = null

    /** Returns whether the controller is currently visible.  */
    val isVisible: Boolean
        get() = visibility == View.VISIBLE

    private val isPlaying: Boolean
        get() = (this.player != null
                && this.player!!.playbackState != Player.STATE_ENDED
                && this.player!!.playbackState != Player.STATE_IDLE
                && this.player!!.playWhenReady)

    /** Listener to be notified about changes of the visibility of the UI control.  */
    interface VisibilityListener {

        /**
         * Called when the visibility changes.
         *
         * @param visibility The new visibility. Either [View.VISIBLE] or [View.GONE].
         */
        fun onVisibilityChange(visibility: Int)
    }

    /**
     * Listener to be notified when quality change button is clicked
     *
     * @author Dragos
     * @since 06-Mar-19
     */
    interface OnQualityChangeClickedListener {
        fun onQualityChangeClicked(qualityChangeView: ImageView)
    }

    /**
     * Listener to be notified when full screen button is clicked
     *
     * @author Dragos
     * @since 06-Mar-19
     */
    interface OnFullScreenButtonClickedListener {
        fun onFullScreenButtonClicked(fullScreenView: ImageView)
    }

    init {
        var controllerLayoutId = R.layout.exo_player_control_view
        rewindMs = DEFAULT_REWIND_MS
        fastForwardMs = DEFAULT_FAST_FORWARD_MS
        showTimeoutMs = DEFAULT_SHOW_TIMEOUT_MS
        repeatToggleModes = DEFAULT_REPEAT_TOGGLE_MODES
        hideAtMs = C.TIME_UNSET
        showShuffleButton = false
        if (playbackAttrs != null) {
            val a = context
                    .theme
                    .obtainStyledAttributes(playbackAttrs, R.styleable.PlayerControlView, 0, 0)
            try {
                rewindMs = a.getInt(R.styleable.PlayerControlView_rewind_increment, rewindMs)
                fastForwardMs = a.getInt(R.styleable.PlayerControlView_fastforward_increment, fastForwardMs)
                showTimeoutMs = a.getInt(R.styleable.PlayerControlView_show_timeout, showTimeoutMs)
                controllerLayoutId = a.getResourceId(R.styleable.PlayerControlView_controller_layout_id, controllerLayoutId)
                repeatToggleModes = getRepeatToggleModes(a, repeatToggleModes)
                showShuffleButton = a.getBoolean(R.styleable.PlayerControlView_show_shuffle_button, showShuffleButton)
            } finally {
                a.recycle()
            }
        }
        period = Timeline.Period()
        window = Timeline.Window()
        formatBuilder = StringBuilder()
        formatter = Formatter(formatBuilder, Locale.getDefault())
        adGroupTimesMs = LongArray(0)
        playedAdGroups = BooleanArray(0)
        extraAdGroupTimesMs = LongArray(0)
        extraPlayedAdGroups = BooleanArray(0)
        componentListener = ComponentListener()
        controlDispatcher = com.google.android.exoplayer2.DefaultControlDispatcher()
        updateProgressAction = Runnable { this.updateProgress() }
        hideAction = Runnable { this.hide() }

        LayoutInflater.from(context).inflate(controllerLayoutId, this)
        descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS

        durationView = findViewById(R.id.exo_duration)
        positionView = findViewById(R.id.exo_position)
        timeBar = findViewById<View>(R.id.exo_progress) as TimeBar?
        timeBar?.addListener(componentListener)
        playButton = findViewById(R.id.exo_play)
        playButton?.setOnClickListener(componentListener)
        pauseButton = findViewById(R.id.exo_pause)
        pauseButton?.setOnClickListener(componentListener)
        previousButton = findViewById(R.id.exo_prev)
        previousButton?.setOnClickListener(componentListener)
        nextButton = findViewById(R.id.exo_next)
        nextButton?.setOnClickListener(componentListener)
        rewindButton = findViewById(R.id.exo_rew)
        rewindButton?.setOnClickListener(componentListener)
        fastForwardButton = findViewById(R.id.exo_ffwd)
        fastForwardButton?.setOnClickListener(componentListener)
        repeatToggleButton = findViewById(R.id.exo_repeat_toggle)
        repeatToggleButton?.setOnClickListener(componentListener)
        qualityChangeView = findViewById(R.id.exo_quality_change)
        qualityChangeView?.setOnClickListener(componentListener)
        fullscreenView = findViewById(R.id.exo_fullscreen_btn)
        fullscreenView?.setOnClickListener(componentListener)
        shuffleButton = findViewById(R.id.exo_shuffle)
        shuffleButton?.setOnClickListener(componentListener)
        val resources = context.resources
        repeatOffButtonDrawable = ResourcesCompat.getDrawable(resources, R.drawable.exo_controls_repeat_off, null)!!
        repeatOneButtonDrawable = ResourcesCompat.getDrawable(resources, R.drawable.exo_controls_repeat_one, null)!!
        repeatAllButtonDrawable = ResourcesCompat.getDrawable(resources, R.drawable.exo_controls_repeat_all, null)!!
        repeatOffButtonContentDescription = resources.getString(R.string.exo_controls_repeat_off_description)
        repeatOneButtonContentDescription = resources.getString(R.string.exo_controls_repeat_one_description)
        repeatAllButtonContentDescription = resources.getString(R.string.exo_controls_repeat_all_description)
    }

    /**
     * Sets whether the time bar should show all windows, as opposed to just the current one. If the
     * timeline has a period with unknown duration or more than [ ][.MAX_WINDOWS_FOR_MULTI_WINDOW_TIME_BAR] windows the time bar will fall back to showing a single
     * window.
     *
     * @param showMultiWindowTimeBar Whether the time bar should show all windows.
     */
    fun setShowMultiWindowTimeBar(showMultiWindowTimeBar: Boolean) {
        this.showMultiWindowTimeBar = showMultiWindowTimeBar
        updateTimeBarMode()
    }

    /**
     * Sets the millisecond positions of extra ad markers relative to the start of the window (or
     * timeline, if in multi-window mode) and whether each extra ad has been played or not. The
     * markers are shown in addition to any ad markers for ads in the player's timeline.
     *
     * @param extraAdGroupTimesMs The millisecond timestamps of the extra ad markers to show, or
     * `null` to show no extra ad markers.
     * @param extraPlayedAdGroups Whether each ad has been played, or `null` to show no extra ad
     * markers.
     */
    fun setExtraAdGroupMarkers(
            extraAdGroupTimesMs: LongArray?, extraPlayedAdGroups: BooleanArray?) {
        if (extraAdGroupTimesMs == null) {
            this.extraAdGroupTimesMs = LongArray(0)
            this.extraPlayedAdGroups = BooleanArray(0)
        } else {
            Assertions.checkArgument(extraAdGroupTimesMs.size == extraPlayedAdGroups!!.size)
            this.extraAdGroupTimesMs = extraAdGroupTimesMs
            this.extraPlayedAdGroups = extraPlayedAdGroups
        }
        updateProgress()
    }

    /**
     * Sets the [VisibilityListener].
     *
     * @param listener The listener to be notified about visibility changes.
     */
    fun setVisibilityListener(listener: VisibilityListener) {
        this.visibilityListener = listener
    }

    /**
     * Sets the [PlaybackPreparer].
     *
     * @param playbackPreparer The [PlaybackPreparer].
     */
    fun setPlaybackPreparer(playbackPreparer: PlaybackPreparer?) {
        this.playbackPreparer = playbackPreparer
    }

    /**
     * Sets the [com.google.android.exoplayer2.ControlDispatcher].
     *
     * @param controlDispatcher The [com.google.android.exoplayer2.ControlDispatcher], or null
     * to use [com.google.android.exoplayer2.DefaultControlDispatcher].
     */
    fun setControlDispatcher(
            controlDispatcher: com.google.android.exoplayer2.ControlDispatcher?) {
        this.controlDispatcher = controlDispatcher
                ?: com.google.android.exoplayer2.DefaultControlDispatcher()
    }

    /**
     * Sets the rewind increment in milliseconds.
     *
     * @param rewindMs The rewind increment in milliseconds. A non-positive value will cause the
     * rewind button to be disabled.
     */
    fun setRewindIncrementMs(rewindMs: Int) {
        this.rewindMs = rewindMs
        updateNavigation()
    }

    /**
     * Sets the fast forward increment in milliseconds.
     *
     * @param fastForwardMs The fast forward increment in milliseconds. A non-positive value will
     * cause the fast forward button to be disabled.
     */
    fun setFastForwardIncrementMs(fastForwardMs: Int) {
        this.fastForwardMs = fastForwardMs
        updateNavigation()
    }

    /**
     * Returns the playback controls timeout. The playback controls are automatically hidden after
     * this duration of time has elapsed without user input.
     *
     * @return The duration in milliseconds. A non-positive value indicates that the controls will
     * remain visible indefinitely.
     */
    fun getShowTimeoutMs(): Int {
        return showTimeoutMs
    }

    /**
     * Sets the playback controls timeout. The playback controls are automatically hidden after this
     * duration of time has elapsed without user input.
     *
     * @param showTimeoutMs The duration in milliseconds. A non-positive value will cause the controls
     * to remain visible indefinitely.
     */
    fun setShowTimeoutMs(showTimeoutMs: Int) {
        this.showTimeoutMs = showTimeoutMs
        if (isVisible) {
            // Reset the timeout.
            hideAfterTimeout()
        }
    }

    /**
     * Returns which repeat toggle modes are enabled.
     *
     * @return The currently enabled [RepeatModeUtil.RepeatToggleModes].
     */
    @RepeatModeUtil.RepeatToggleModes
    fun getRepeatToggleModes(): Int {
        return repeatToggleModes
    }

    /**
     * Sets which repeat toggle modes are enabled.
     *
     * @param repeatToggleModes A set of [RepeatModeUtil.RepeatToggleModes].
     */
    fun setRepeatToggleModes(@RepeatModeUtil.RepeatToggleModes repeatToggleModes: Int) {
        this.repeatToggleModes = repeatToggleModes
        if (this.player != null) {
            @Player.RepeatMode val currentMode = this.player!!.repeatMode
            if (repeatToggleModes == RepeatModeUtil.REPEAT_TOGGLE_MODE_NONE && currentMode != Player.REPEAT_MODE_OFF) {
                controlDispatcher!!.dispatchSetRepeatMode(this.player, Player.REPEAT_MODE_OFF)
            } else if (repeatToggleModes == RepeatModeUtil.REPEAT_TOGGLE_MODE_ONE && currentMode == Player.REPEAT_MODE_ALL) {
                controlDispatcher!!.dispatchSetRepeatMode(this.player, Player.REPEAT_MODE_ONE)
            } else if (repeatToggleModes == RepeatModeUtil.REPEAT_TOGGLE_MODE_ALL && currentMode == Player.REPEAT_MODE_ONE) {
                controlDispatcher!!.dispatchSetRepeatMode(this.player, Player.REPEAT_MODE_ALL)
            }
        }
        updateRepeatModeButton()
    }

    /** Returns whether the shuffle button is shown.  */
    fun getShowShuffleButton(): Boolean {
        return showShuffleButton
    }

    /**
     * Sets whether the shuffle button is shown.
     *
     * @param showShuffleButton Whether the shuffle button is shown.
     */
    fun setShowShuffleButton(showShuffleButton: Boolean) {
        this.showShuffleButton = showShuffleButton
        updateShuffleButton()
    }

    /**
     * Shows the playback controls. If [.getShowTimeoutMs] is positive then the controls will
     * be automatically hidden after this duration of time has elapsed without user input.
     */
    fun show() {
        if (!isVisible) {
            visibility = View.VISIBLE
            if (visibilityListener != null) {
                visibilityListener!!.onVisibilityChange(visibility)
            }
            updateAll()
            requestPlayPauseFocus()
        }
        // Call hideAfterTimeout even if already visible to reset the timeout.
        hideAfterTimeout()
    }

    /** Hides the controller.  */
    fun hide() {
        if (isVisible) {
            visibility = View.GONE
            if (visibilityListener != null) {
                visibilityListener!!.onVisibilityChange(visibility)
            }
            removeCallbacks(updateProgressAction)
            removeCallbacks(hideAction)
            hideAtMs = C.TIME_UNSET
        }
    }

    private fun hideAfterTimeout() {
        removeCallbacks(hideAction)
        if (showTimeoutMs > 0) {
            hideAtMs = SystemClock.uptimeMillis() + showTimeoutMs
            if (_isAttachedToWindow) {
                postDelayed(hideAction, showTimeoutMs.toLong())
            }
        } else {
            hideAtMs = C.TIME_UNSET
        }
    }

    private fun updateAll() {
        updatePlayPauseButton()
        updateNavigation()
        updateRepeatModeButton()
        updateShuffleButton()
        updateProgress()
    }

    private fun updatePlayPauseButton() {
        if (!isVisible || !_isAttachedToWindow) {
            return
        }
        var requestPlayPauseFocus = false
        val playing = isPlaying
        if (playButton != null) {
            requestPlayPauseFocus = requestPlayPauseFocus or (playing && playButton.isFocused)
            playButton.visibility = if (playing) View.GONE else View.VISIBLE
        }
        if (pauseButton != null) {
            requestPlayPauseFocus = requestPlayPauseFocus or (!playing && pauseButton.isFocused)
            pauseButton.visibility = if (!playing) View.GONE else View.VISIBLE
        }
        if (requestPlayPauseFocus) {
            requestPlayPauseFocus()
        }
    }

    private fun updateNavigation() {
        if (!isVisible || !_isAttachedToWindow) {
            return
        }
        val timeline = if (this.player != null) this.player!!.currentTimeline else null
        val haveNonEmptyTimeline = timeline != null && !timeline.isEmpty
        var isSeekable = false
        var enablePrevious = false
        var enableNext = false
        if (haveNonEmptyTimeline && !this.player!!.isPlayingAd) {
            val windowIndex = this.player!!.currentWindowIndex
            timeline!!.getWindow(windowIndex, window)
            isSeekable = window.isSeekable
            enablePrevious = isSeekable || !window.isDynamic || this.player!!.hasPrevious()
            enableNext = window.isDynamic || this.player!!.hasNext()
        }
        setButtonEnabled(enablePrevious, previousButton)
        setButtonEnabled(enableNext, nextButton)
        setButtonEnabled(fastForwardMs > 0 && isSeekable, fastForwardButton)
        setButtonEnabled(rewindMs > 0 && isSeekable, rewindButton)
        timeBar?.setEnabled(isSeekable)
    }

    private fun updateRepeatModeButton() {
        if (!isVisible || !_isAttachedToWindow || repeatToggleButton == null) {
            return
        }
        if (repeatToggleModes == RepeatModeUtil.REPEAT_TOGGLE_MODE_NONE) {
            repeatToggleButton.visibility = View.GONE
            return
        }
        if (this.player == null) {
            setButtonEnabled(false, repeatToggleButton)
            return
        }
        setButtonEnabled(true, repeatToggleButton)
        when (this.player!!.repeatMode) {
            Player.REPEAT_MODE_OFF -> {
                repeatToggleButton.setImageDrawable(repeatOffButtonDrawable)
                repeatToggleButton.contentDescription = repeatOffButtonContentDescription
            }
            Player.REPEAT_MODE_ONE -> {
                repeatToggleButton.setImageDrawable(repeatOneButtonDrawable)
                repeatToggleButton.contentDescription = repeatOneButtonContentDescription
            }
            Player.REPEAT_MODE_ALL -> {
                repeatToggleButton.setImageDrawable(repeatAllButtonDrawable)
                repeatToggleButton.contentDescription = repeatAllButtonContentDescription
            }
        }// Never happens.
        repeatToggleButton.visibility = View.VISIBLE
    }

    private fun updateShuffleButton() {
        if (!isVisible || !_isAttachedToWindow || shuffleButton == null) {
            return
        }
        if (!showShuffleButton) {
            shuffleButton.visibility = View.GONE
        } else if (this.player == null) {
            setButtonEnabled(false, shuffleButton)
        } else {
            shuffleButton.alpha = if (this.player!!.shuffleModeEnabled) 1f else 0.3f
            shuffleButton.isEnabled = true
            shuffleButton.visibility = View.VISIBLE
        }
    }

    private fun updateTimeBarMode() {
        if (this.player == null) {
            return
        }
        multiWindowTimeBar = showMultiWindowTimeBar && canShowMultiWindowTimeBar(this.player!!.currentTimeline, window)
    }

    private fun updateProgress() {
        if (!isVisible || !_isAttachedToWindow) {
            return
        }

        var position: Long = 0
        var bufferedPosition: Long = 0
        var duration: Long = 0
        if (this.player != null) {
            var currentWindowTimeBarOffsetMs: Long = 0
            var durationUs: Long = 0
            var adGroupCount = 0
            val timeline = this.player!!.currentTimeline
            if (!timeline.isEmpty) {
                val currentWindowIndex = this.player!!.currentWindowIndex
                val firstWindowIndex = if (multiWindowTimeBar) 0 else currentWindowIndex
                val lastWindowIndex = if (multiWindowTimeBar) timeline.windowCount - 1 else currentWindowIndex
                for (i in firstWindowIndex..lastWindowIndex) {
                    if (i == currentWindowIndex) {
                        currentWindowTimeBarOffsetMs = C.usToMs(durationUs)
                    }
                    timeline.getWindow(i, window)
                    if (window.durationUs == C.TIME_UNSET) {
                        Assertions.checkState(!multiWindowTimeBar)
                        break
                    }
                    for (j in window.firstPeriodIndex..window.lastPeriodIndex) {
                        timeline.getPeriod(j, period)
                        val periodAdGroupCount = period.adGroupCount
                        for (adGroupIndex in 0 until periodAdGroupCount) {
                            var adGroupTimeInPeriodUs = period.getAdGroupTimeUs(adGroupIndex)
                            if (adGroupTimeInPeriodUs == C.TIME_END_OF_SOURCE) {
                                if (period.durationUs == C.TIME_UNSET) {
                                    // Don't show ad markers for postrolls in periods with unknown duration.
                                    continue
                                }
                                adGroupTimeInPeriodUs = period.durationUs
                            }
                            val adGroupTimeInWindowUs = adGroupTimeInPeriodUs + period.positionInWindowUs
                            if (adGroupTimeInWindowUs >= 0 && adGroupTimeInWindowUs <= window.durationUs) {
                                if (adGroupCount == adGroupTimesMs!!.size) {
                                    val newLength = if (adGroupTimesMs!!.size == 0) 1 else adGroupTimesMs!!.size * 2
                                    adGroupTimesMs = Arrays.copyOf(adGroupTimesMs!!, newLength)
                                    playedAdGroups = Arrays.copyOf(playedAdGroups!!, newLength)
                                }
                                adGroupTimesMs!![adGroupCount] = C.usToMs(durationUs + adGroupTimeInWindowUs)
                                playedAdGroups!![adGroupCount] = period.hasPlayedAdGroup(adGroupIndex)
                                adGroupCount++
                            }
                        }
                    }
                    durationUs += window.durationUs
                }
            }
            duration = C.usToMs(durationUs)
            position = currentWindowTimeBarOffsetMs + this.player!!.contentPosition
            bufferedPosition = currentWindowTimeBarOffsetMs + this.player!!.contentBufferedPosition
            if (timeBar != null) {
                val extraAdGroupCount = extraAdGroupTimesMs!!.size
                val totalAdGroupCount = adGroupCount + extraAdGroupCount
                if (totalAdGroupCount > adGroupTimesMs!!.size) {
                    adGroupTimesMs = adGroupTimesMs!!.copyOf(totalAdGroupCount)
                    playedAdGroups = playedAdGroups!!.copyOf(totalAdGroupCount)
                }
                System.arraycopy(extraAdGroupTimesMs!!, 0, adGroupTimesMs!!, adGroupCount, extraAdGroupCount)
                System.arraycopy(extraPlayedAdGroups!!, 0, playedAdGroups!!, adGroupCount, extraAdGroupCount)
                timeBar.setAdGroupTimesMs(adGroupTimesMs, playedAdGroups, totalAdGroupCount)
            }
        }
        if (durationView != null) {
            durationView.text = Util.getStringForTime(formatBuilder, formatter, duration)
        }
        if (positionView != null && !scrubbing) {
            positionView.text = Util.getStringForTime(formatBuilder, formatter, position)
        }
        if (timeBar != null) {
            timeBar.setPosition(position)
            timeBar.setBufferedPosition(bufferedPosition)
            timeBar.setDuration(duration)
        }

        // Cancel any pending updates and schedule a new one if necessary.
        removeCallbacks(updateProgressAction)
        val playbackState = if (this.player == null) Player.STATE_IDLE else this.player!!.playbackState
        if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
            val delayMs: Long
            if (this.player!!.playWhenReady && playbackState == Player.STATE_READY) {
                val playbackSpeed = this.player!!.playbackParameters.speed
                if (playbackSpeed <= 0.1f) {
                    delayMs = 1000
                } else if (playbackSpeed <= 5f) {
                    val mediaTimeUpdatePeriodMs = (1000 / Math.max(1, Math.round(1 / playbackSpeed))).toLong()
                    var mediaTimeDelayMs = mediaTimeUpdatePeriodMs - position % mediaTimeUpdatePeriodMs
                    if (mediaTimeDelayMs < mediaTimeUpdatePeriodMs / 5) {
                        mediaTimeDelayMs += mediaTimeUpdatePeriodMs
                    }
                    delayMs = if (playbackSpeed == 1f) mediaTimeDelayMs else (mediaTimeDelayMs / playbackSpeed).toLong()
                } else {
                    delayMs = 200
                }
            } else {
                delayMs = 1000
            }
            postDelayed(updateProgressAction, delayMs)
        }
    }

    private fun requestPlayPauseFocus() {
        val playing = isPlaying
        if (!playing && playButton != null) {
            playButton.requestFocus()
        } else if (playing && pauseButton != null) {
            pauseButton.requestFocus()
        }
    }

    private fun setButtonEnabled(enabled: Boolean, view: View?) {
        if (view == null) {
            return
        }
        view.isEnabled = enabled
        view.alpha = if (enabled) 1f else 0.3f
        view.visibility = View.VISIBLE
    }

    private fun previous() {
        val timeline = this.player!!.currentTimeline
        if (timeline.isEmpty || this.player!!.isPlayingAd) {
            return
        }
        val windowIndex = this.player!!.currentWindowIndex
        timeline.getWindow(windowIndex, window)
        val previousWindowIndex = this.player!!.previousWindowIndex
        if (previousWindowIndex != C.INDEX_UNSET && (this.player!!.currentPosition <= MAX_POSITION_FOR_SEEK_TO_PREVIOUS || window.isDynamic && !window.isSeekable)) {
            seekTo(previousWindowIndex, C.TIME_UNSET)
        } else {
            seekTo(0)
        }
    }

    private operator fun next() {
        val timeline = this.player!!.currentTimeline
        if (timeline.isEmpty || this.player!!.isPlayingAd) {
            return
        }
        val windowIndex = this.player!!.currentWindowIndex
        val nextWindowIndex = this.player!!.nextWindowIndex
        if (nextWindowIndex != C.INDEX_UNSET) {
            seekTo(nextWindowIndex, C.TIME_UNSET)
        } else if (timeline.getWindow(windowIndex, window).isDynamic) {
            seekTo(windowIndex, C.TIME_UNSET)
        }
    }

    private fun rewind() {
        if (rewindMs <= 0) {
            return
        }
        seekTo(Math.max(this.player!!.currentPosition - rewindMs, 0))
    }

    private fun fastForward() {
        if (fastForwardMs <= 0) {
            return
        }
        val durationMs = this.player!!.duration
        var seekPositionMs = this.player!!.currentPosition + fastForwardMs
        if (durationMs != C.TIME_UNSET) {
            seekPositionMs = Math.min(seekPositionMs, durationMs)
        }
        seekTo(seekPositionMs)
    }

    private fun seekTo(positionMs: Long) {
        seekTo(this.player!!.currentWindowIndex, positionMs)
    }

    private fun seekTo(windowIndex: Int, positionMs: Long) {
        val dispatched = controlDispatcher!!.dispatchSeekTo(this.player, windowIndex, positionMs)
        if (!dispatched) {
            // The seek wasn't dispatched. If the progress bar was dragged by the user to perform the
            // seek then it'll now be in the wrong position. Trigger a progress update to snap it back.
            updateProgress()
        }
    }

    private fun seekToTimeBarPosition(positionMs: Long) {
        var positionMs = positionMs
        var windowIndex: Int
        val timeline = this.player!!.currentTimeline
        if (multiWindowTimeBar && !timeline.isEmpty) {
            val windowCount = timeline.windowCount
            windowIndex = 0
            while (true) {
                val windowDurationMs = timeline.getWindow(windowIndex, window).durationMs
                if (positionMs < windowDurationMs) {
                    break
                } else if (windowIndex == windowCount - 1) {
                    // Seeking past the end of the last window should seek to the end of the timeline.
                    positionMs = windowDurationMs
                    break
                }
                positionMs -= windowDurationMs
                windowIndex++
            }
        } else {
            windowIndex = this.player!!.currentWindowIndex
        }
        seekTo(windowIndex, positionMs)
    }

    public override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        _isAttachedToWindow = true
        if (hideAtMs != C.TIME_UNSET) {
            val delayMs = hideAtMs - SystemClock.uptimeMillis()
            if (delayMs <= 0) {
                hide()
            } else {
                postDelayed(hideAction, delayMs)
            }
        } else if (isVisible) {
            hideAfterTimeout()
        }
        updateAll()
    }

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        _isAttachedToWindow = false
        removeCallbacks(updateProgressAction)
        removeCallbacks(hideAction)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            removeCallbacks(hideAction)
        } else if (ev.action == MotionEvent.ACTION_UP) {
            hideAfterTimeout()
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return dispatchMediaKeyEvent(event) || super.dispatchKeyEvent(event)
    }

    /**
     * Called to process media key events. Any [KeyEvent] can be passed but only media key
     * events will be handled.
     *
     * @param event A key event.
     * @return Whether the key event was handled.
     */
    fun dispatchMediaKeyEvent(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        if (this.player == null || !isHandledMediaKey(keyCode)) {
            return false
        }
        if (event.action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
                fastForward()
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND) {
                rewind()
            } else if (event.repeatCount == 0) {
                when (keyCode) {
                    KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> controlDispatcher!!.dispatchSetPlayWhenReady(this.player, !this.player!!.playWhenReady)
                    KeyEvent.KEYCODE_MEDIA_PLAY -> controlDispatcher!!.dispatchSetPlayWhenReady(this.player, true)
                    KeyEvent.KEYCODE_MEDIA_PAUSE -> controlDispatcher!!.dispatchSetPlayWhenReady(this.player, false)
                    KeyEvent.KEYCODE_MEDIA_NEXT -> next()
                    KeyEvent.KEYCODE_MEDIA_PREVIOUS -> previous()
                    else -> {
                    }
                }
            }
        }
        return true
    }

    var onFullScreenButtonClickedListener: OnFullScreenButtonClickedListener? = null
        set(value) {
            field = value
            fullscreenView?.let {
                if (field == null) visibility = View.GONE
                else visibility = View.VISIBLE
            }
        }

    var onQualityChangeClickedListener: OnQualityChangeClickedListener? = null
        set(value) {
            field = value
            qualityChangeView?.let {
                if (field == null) visibility = View.GONE
                else visibility = View.VISIBLE
            }
        }

    private inner class ComponentListener : Player.EventListener, TimeBar.OnScrubListener, View.OnClickListener {

        override fun onScrubStart(timeBar: TimeBar, position: Long) {
            scrubbing = true
        }

        override fun onScrubMove(timeBar: TimeBar, position: Long) {
            if (positionView != null) {
                positionView.text = Util.getStringForTime(formatBuilder, formatter, position)
            }
        }

        override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
            scrubbing = false
            if (!canceled && this@PlayerControlView.player != null) {
                seekToTimeBarPosition(position)
            }
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            updatePlayPauseButton()
            updateProgress()
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            updateRepeatModeButton()
            updateNavigation()
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            updateShuffleButton()
            updateNavigation()
        }

        override fun onPositionDiscontinuity(@Player.DiscontinuityReason reason: Int) {
            updateNavigation()
            updateProgress()
        }

        override fun onTimelineChanged(
                timeline: Timeline, manifest: Any?, @Player.TimelineChangeReason reason: Int) {
            updateNavigation()
            updateTimeBarMode()
            updateProgress()
        }

        override fun onClick(view: View) {
            if (this@PlayerControlView.player != null) {
                when {
                    nextButton === view -> next()
                    previousButton === view -> previous()
                    fastForwardButton === view -> fastForward()
                    rewindButton === view -> rewind()
                    playButton === view -> {
                        if (this@PlayerControlView.player!!.playbackState == Player.STATE_IDLE) {
                            if (playbackPreparer != null) {
                                playbackPreparer!!.preparePlayback()
                            }
                        } else if (this@PlayerControlView.player!!.playbackState == Player.STATE_ENDED) {
                            controlDispatcher!!.dispatchSeekTo(
                                    this@PlayerControlView.player,
                                    this@PlayerControlView.player!!.currentWindowIndex,
                                    C.TIME_UNSET
                            )
                        }
                        controlDispatcher!!.dispatchSetPlayWhenReady(
                                this@PlayerControlView.player,
                                true
                        )
                    }
                    pauseButton === view -> controlDispatcher!!.dispatchSetPlayWhenReady(
                            this@PlayerControlView.player,
                            false
                    )
                    repeatToggleButton === view -> controlDispatcher!!.dispatchSetRepeatMode(
                            this@PlayerControlView.player, RepeatModeUtil.getNextRepeatMode(
                            this@PlayerControlView.player!!.repeatMode,
                            repeatToggleModes
                    ))
                    shuffleButton === view -> controlDispatcher!!.dispatchSetShuffleModeEnabled(
                            this@PlayerControlView.player,
                            !this@PlayerControlView.player!!.shuffleModeEnabled
                    )
                    qualityChangeView === view -> onQualityChangeClickedListener?.onQualityChangeClicked(qualityChangeView)
                    fullscreenView === view -> onFullScreenButtonClickedListener?.onFullScreenButtonClicked(fullscreenView)
                }
            }
        }
    }

    companion object {

        init {
            ExoPlayerLibraryInfo.registerModule("goog.exo.ui")
        }

        /** The default fast forward increment, in milliseconds.  */
        const val DEFAULT_FAST_FORWARD_MS = 15000
        /** The default rewind increment, in milliseconds.  */
        const val DEFAULT_REWIND_MS = 5000
        /** The default show timeout, in milliseconds.  */
        const val DEFAULT_SHOW_TIMEOUT_MS = 5000
        /** The default repeat toggle modes.  */
        @RepeatModeUtil.RepeatToggleModes
        val DEFAULT_REPEAT_TOGGLE_MODES = RepeatModeUtil.REPEAT_TOGGLE_MODE_NONE

        /** The maximum number of windows that can be shown in a multi-window time bar.  */
        const val MAX_WINDOWS_FOR_MULTI_WINDOW_TIME_BAR = 100

        private const val MAX_POSITION_FOR_SEEK_TO_PREVIOUS: Long = 3000

        @RepeatModeUtil.RepeatToggleModes
        private fun getRepeatToggleModes(
                a: TypedArray, @RepeatModeUtil.RepeatToggleModes repeatToggleModes: Int): Int {
            return a.getInt(R.styleable.PlayerControlView_repeat_toggle_modes, repeatToggleModes)
        }

        @SuppressLint("InlinedApi")
        private fun isHandledMediaKey(keyCode: Int): Boolean {
            return (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
                    || keyCode == KeyEvent.KEYCODE_MEDIA_REWIND
                    || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                    || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY
                    || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE
                    || keyCode == KeyEvent.KEYCODE_MEDIA_NEXT
                    || keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS)
        }

        /**
         * Returns whether the specified `timeline` can be shown on a multi-window time bar.
         *
         * @param timeline The [Timeline] to check.
         * @param window A scratch [Timeline.Window] instance.
         * @return Whether the specified timeline can be shown on a multi-window time bar.
         */
        private fun canShowMultiWindowTimeBar(timeline: Timeline, window: Timeline.Window): Boolean {
            if (timeline.windowCount > MAX_WINDOWS_FOR_MULTI_WINDOW_TIME_BAR) {
                return false
            }
            val windowCount = timeline.windowCount
            for (i in 0 until windowCount) {
                if (timeline.getWindow(i, window).durationUs == C.TIME_UNSET) {
                    return false
                }
            }
            return true
        }
    }
}
