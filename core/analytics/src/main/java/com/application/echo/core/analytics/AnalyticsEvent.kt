package com.application.echo.core.analytics

/**
 * Represents an analytics event with its name and parameters.
 *
 * @property name The name of the event
 * @property parameters Optional parameters associated with the event
 */
data class AnalyticsEvent(
    val name: String,
    val parameters: Map<String, Any> = emptyMap()
) {
    /**
     * Screen tracking events
     */
    object ScreenEvent {
        const val HOME = "home"
        const val PROFILE = "profile"
        const val SETTINGS = "settings"
        const val LOGIN = "login"
        const val SIGNUP = "signup"
        const val FEED = "feed"
        const val SEARCH = "search"
        const val MESSAGES = "messages"
        const val NOTIFICATIONS = "notifications"
        const val CREATE_POST = "create_post"
        const val EDIT_PROFILE = "edit_profile"
        const val USER_PROFILE = "user_profile"
        const val POST_DETAILS = "post_details"
        const val STORIES = "stories"
        const val DISCOVER = "discover"

        fun view(screenName: String) = AnalyticsEvent(
            name = "screen_view",
            parameters = mapOf("screen_name" to screenName)
        )
    }

    /**
     * Button click events for tracking user interactions
     */
    object ButtonEvent {
        // Authentication buttons
        fun login() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "login_button")
        )

        fun signup() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "signup_button")
        )

        fun logout() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "logout_button")
        )

        fun forgotPassword() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "forgot_password_button")
        )

        // Content interaction buttons
        fun like(postId: String, isLiked: Boolean) = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf(
                "button_name" to "like_button",
                "post_id" to postId,
                "is_liked" to isLiked
            )
        )

        fun comment(postId: String) = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf(
                "button_name" to "comment_button",
                "post_id" to postId
            )
        )

        fun share(postId: String, shareMethod: String) = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf(
                "button_name" to "share_button",
                "post_id" to postId,
                "share_method" to shareMethod
            )
        )

        fun save(postId: String, isSaved: Boolean) = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf(
                "button_name" to "save_button",
                "post_id" to postId,
                "is_saved" to isSaved
            )
        )

        fun follow(userId: String, isFollowing: Boolean) = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf(
                "button_name" to "follow_button",
                "user_id" to userId,
                "is_following" to isFollowing
            )
        )

        // Post creation buttons
        fun createPost() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "create_post_button")
        )

        fun publishPost() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "publish_post_button")
        )

        fun addPhoto() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "add_photo_button")
        )

        fun addVideo() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "add_video_button")
        )

        fun addLocation() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "add_location_button")
        )

        fun addTag() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "add_tag_button")
        )

        // Navigation buttons
        fun homeTab() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "home_tab_button")
        )

        fun searchTab() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "search_tab_button")
        )

        fun messagesTab() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "messages_tab_button")
        )

        fun notificationsTab() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "notifications_tab_button")
        )

        fun profileTab() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "profile_tab_button")
        )

        fun back() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "back_button")
        )

        fun close() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "close_button")
        )

        // Settings and profile buttons
        fun editProfile() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "edit_profile_button")
        )

        fun settings() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "settings_button")
        )

        fun privacySettings() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "privacy_settings_button")
        )

        fun notificationSettings() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "notification_settings_button")
        )

        fun blockUser() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "block_user_button")
        )

        fun reportContent() = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to "report_content_button")
        )

        // Custom button with dynamic parameters
        fun custom(name: String, additionalParams: Map<String, Any> = emptyMap()) = AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to name) + additionalParams
        )
    }

    /**
     * Navigation events for tracking user flow
     */
    object NavigationEvent {
        fun screenTransition(
            fromScreen: String,
            toScreen: String,
            trigger: String = "user_action"
        ) = AnalyticsEvent(
            name = "screen_transition",
            parameters = mapOf(
                "from_screen" to fromScreen,
                "to_screen" to toScreen,
                "trigger" to trigger
            )
        )

        fun deepLink(url: String, source: String) = AnalyticsEvent(
            name = "deep_link_opened",
            parameters = mapOf(
                "url" to url,
                "source" to source
            )
        )

        fun externalLink(url: String, destination: String) = AnalyticsEvent(
            name = "external_link_opened",
            parameters = mapOf(
                "url" to url,
                "destination" to destination
            )
        )

        fun tabSwitch(fromTab: String, toTab: String) = AnalyticsEvent(
            name = "tab_switched",
            parameters = mapOf(
                "from_tab" to fromTab,
                "to_tab" to toTab
            )
        )

        fun back(fromScreen: String, toScreen: String) = AnalyticsEvent(
            name = "back_navigation",
            parameters = mapOf(
                "from_screen" to fromScreen,
                "to_screen" to toScreen
            )
        )
    }

    /**
     * Content interaction events for social media features
     */
    object ContentEvent {
        fun postViewed(postId: String, authorId: String, postType: String, viewDuration: Long) =
            AnalyticsEvent(
                name = "post_viewed",
                parameters = mapOf(
                    "post_id" to postId,
                    "author_id" to authorId,
                    "post_type" to postType,
                    "view_duration_ms" to viewDuration
                )
            )

        fun postLiked(postId: String, authorId: String, isLiked: Boolean) = AnalyticsEvent(
            name = "post_liked",
            parameters = mapOf(
                "post_id" to postId,
                "author_id" to authorId,
                "is_liked" to isLiked
            )
        )

        fun postShared(postId: String, authorId: String, shareMethod: String) = AnalyticsEvent(
            name = "post_shared",
            parameters = mapOf(
                "post_id" to postId,
                "author_id" to authorId,
                "share_method" to shareMethod
            )
        )

        fun postSaved(postId: String, authorId: String, isSaved: Boolean) = AnalyticsEvent(
            name = "post_saved",
            parameters = mapOf(
                "post_id" to postId,
                "author_id" to authorId,
                "is_saved" to isSaved
            )
        )

        fun commentAdded(postId: String, authorId: String, commentLength: Int) = AnalyticsEvent(
            name = "comment_added",
            parameters = mapOf(
                "post_id" to postId,
                "author_id" to authorId,
                "comment_length" to commentLength
            )
        )

        fun commentLiked(commentId: String, postId: String, isLiked: Boolean) = AnalyticsEvent(
            name = "comment_liked",
            parameters = mapOf(
                "comment_id" to commentId,
                "post_id" to postId,
                "is_liked" to isLiked
            )
        )

        fun postReported(postId: String, authorId: String, reason: String) = AnalyticsEvent(
            name = "post_reported",
            parameters = mapOf(
                "post_id" to postId,
                "author_id" to authorId,
                "reason" to reason
            )
        )

        fun storyViewed(storyId: String, authorId: String, viewDuration: Long) = AnalyticsEvent(
            name = "story_viewed",
            parameters = mapOf(
                "story_id" to storyId,
                "author_id" to authorId,
                "view_duration_ms" to viewDuration
            )
        )

        fun mediaInteraction(mediaId: String, mediaType: String, action: String) = AnalyticsEvent(
            name = "media_interaction",
            parameters = mapOf(
                "media_id" to mediaId,
                "media_type" to mediaType,
                "action" to action
            )
        )
    }

    /**
     * User engagement events for tracking social interactions
     */
    object UserEvent {
        fun followed(userId: String, isFollowing: Boolean, source: String) = AnalyticsEvent(
            name = "user_followed",
            parameters = mapOf(
                "user_id" to userId,
                "is_following" to isFollowing,
                "source" to source
            )
        )

        fun profileViewed(userId: String, source: String) = AnalyticsEvent(
            name = "profile_viewed",
            parameters = mapOf(
                "user_id" to userId,
                "source" to source
            )
        )

        fun messageSent(recipientId: String, messageType: String, messageLength: Int) =
            AnalyticsEvent(
                name = "message_sent",
                parameters = mapOf(
                    "recipient_id" to recipientId,
                    "message_type" to messageType,
                    "message_length" to messageLength
                )
            )

        fun searchPerformed(query: String, searchType: String, resultCount: Int) = AnalyticsEvent(
            name = "search_performed",
            parameters = mapOf(
                "query" to query,
                "search_type" to searchType,
                "result_count" to resultCount
            )
        )

        fun hashtagClicked(hashtag: String, source: String) = AnalyticsEvent(
            name = "hashtag_clicked",
            parameters = mapOf(
                "hashtag" to hashtag,
                "source" to source
            )
        )

        fun blocked(userId: String, reason: String) = AnalyticsEvent(
            name = "user_blocked",
            parameters = mapOf(
                "user_id" to userId,
                "reason" to reason
            )
        )

        fun notificationInteraction(notificationType: String, action: String) = AnalyticsEvent(
            name = "notification_interaction",
            parameters = mapOf(
                "notification_type" to notificationType,
                "action" to action
            )
        )
    }

    /**
     * Content creation events for tracking post creation flow
     */
    object CreationEvent {
        fun postStarted(postType: String) = AnalyticsEvent(
            name = "post_creation_started",
            parameters = mapOf("post_type" to postType)
        )

        fun postCompleted(
            postId: String,
            postType: String,
            mediaCount: Int,
            hasLocation: Boolean,
            tagCount: Int
        ) = AnalyticsEvent(
            name = "post_creation_completed",
            parameters = mapOf(
                "post_id" to postId,
                "post_type" to postType,
                "media_count" to mediaCount,
                "has_location" to hasLocation,
                "tag_count" to tagCount
            )
        )

        fun postCancelled(postType: String, stage: String) = AnalyticsEvent(
            name = "post_creation_cancelled",
            parameters = mapOf(
                "post_type" to postType,
                "stage" to stage
            )
        )

        fun mediaUploaded(mediaType: String, mediaSize: Long, uploadDuration: Long) =
            AnalyticsEvent(
                name = "media_uploaded",
                parameters = mapOf(
                    "media_type" to mediaType,
                    "media_size_bytes" to mediaSize,
                    "upload_duration_ms" to uploadDuration
                )
            )

        fun filterApplied(filterName: String, mediaType: String) = AnalyticsEvent(
            name = "filter_applied",
            parameters = mapOf(
                "filter_name" to filterName,
                "media_type" to mediaType
            )
        )

        fun locationAdded(locationName: String, accuracy: String) = AnalyticsEvent(
            name = "location_added",
            parameters = mapOf(
                "location_name" to locationName,
                "accuracy" to accuracy
            )
        )

        fun tagAdded(tagType: String, tagValue: String) = AnalyticsEvent(
            name = "tag_added",
            parameters = mapOf(
                "tag_type" to tagType,
                "tag_value" to tagValue
            )
        )

        fun storyCreated(storyId: String, mediaType: String, duration: Long) = AnalyticsEvent(
            name = "story_created",
            parameters = mapOf(
                "story_id" to storyId,
                "media_type" to mediaType,
                "duration_ms" to duration
            )
        )
    }

    /**
     * App lifecycle and performance events
     */
    object AppEvent {
        fun opened(source: String = "unknown") = AnalyticsEvent(
            name = "app_opened",
            parameters = mapOf("source" to source)
        )

        fun closed(sessionDuration: Long) = AnalyticsEvent(
            name = "app_closed",
            parameters = mapOf("session_duration_ms" to sessionDuration)
        )

        fun backgrounded(sessionDuration: Long) = AnalyticsEvent(
            name = "app_backgrounded",
            parameters = mapOf("session_duration_ms" to sessionDuration)
        )

        fun foregrounded(backgroundDuration: Long) = AnalyticsEvent(
            name = "app_foregrounded",
            parameters = mapOf("background_duration_ms" to backgroundDuration)
        )

        fun crashReported(crashType: String, stackTrace: String) = AnalyticsEvent(
            name = "crash_reported",
            parameters = mapOf(
                "crash_type" to crashType,
                "stack_trace" to stackTrace
            )
        )

        fun performanceMetric(metricName: String, value: Double, unit: String) = AnalyticsEvent(
            name = "performance_metric",
            parameters = mapOf(
                "metric_name" to metricName,
                "value" to value,
                "unit" to unit
            )
        )

        fun featureToggled(featureName: String, isEnabled: Boolean) = AnalyticsEvent(
            name = "feature_enabled",
            parameters = mapOf(
                "feature_name" to featureName,
                "is_enabled" to isEnabled
            )
        )
    }

    /**
     * Error and exception tracking events
     */
    object ErrorEvent {
        fun error(errorType: String, errorMessage: String, context: String) = AnalyticsEvent(
            name = "error",
            parameters = mapOf(
                "error_type" to errorType,
                "error_message" to errorMessage,
                "context" to context
            )
        )

        fun exception(exception: String, message: String, stackTrace: String) = AnalyticsEvent(
            name = "exception",
            parameters = mapOf(
                "exception_type" to exception,
                "exception_message" to message,
                "stack_trace" to stackTrace
            )
        )

        fun networkError(endpoint: String, errorCode: Int, errorMessage: String) = AnalyticsEvent(
            name = "network_error",
            parameters = mapOf(
                "endpoint" to endpoint,
                "error_code" to errorCode,
                "error_message" to errorMessage
            )
        )
    }

    /**
     * Performance and timing events
     */
    object TimingEvent {
        fun loadTime(category: String, variable: String, value: Long, label: String = "") =
            AnalyticsEvent(
                name = "timing",
                parameters = mapOf(
                    "category" to category,
                    "variable" to variable,
                    "value" to value,
                    "label" to label
                ).filterValues { it.toString().isNotEmpty() }
            )

        fun apiCall(endpoint: String, method: String, duration: Long, statusCode: Int) =
            AnalyticsEvent(
                name = "api_call",
                parameters = mapOf(
                    "endpoint" to endpoint,
                    "method" to method,
                    "duration_ms" to duration,
                    "status_code" to statusCode
                )
            )

        fun databaseQuery(queryType: String, table: String, duration: Long, resultCount: Int) =
            AnalyticsEvent(
                name = "database_query",
                parameters = mapOf(
                    "query_type" to queryType,
                    "table" to table,
                    "duration_ms" to duration,
                    "result_count" to resultCount
                )
            )
    }
}

/**
 * Extension functions for Analytics class to make event tracking more convenient
 */
fun Analytics.trackButtonClick(
    buttonName: String,
    additionalParams: Map<String, Any> = emptyMap()
) {
    trackEvent(
        AnalyticsEvent(
            name = "button_click",
            parameters = mapOf("button_name" to buttonName) + additionalParams
        )
    )
}

fun Analytics.trackNavigation(from: String, to: String, trigger: String = "user_action") {
    trackEvent(
        AnalyticsEvent(
            name = "navigation",
            parameters = mapOf("from" to from, "to" to to, "trigger" to trigger)
        )
    )
}

fun Analytics.trackContentInteraction(
    action: String,
    contentId: String,
    contentType: String,
    additionalParams: Map<String, Any> = emptyMap()
) {
    trackEvent(
        AnalyticsEvent(
            name = "content_interaction",
            parameters = mapOf(
                "action" to action,
                "content_id" to contentId,
                "content_type" to contentType
            ) + additionalParams
        )
    )
}

fun Analytics.trackUserAction(
    action: String,
    target: String,
    additionalParams: Map<String, Any> = emptyMap()
) {
    trackEvent(
        AnalyticsEvent(
            name = "user_action",
            parameters = mapOf("action" to action, "target" to target) + additionalParams
        )
    )
}

fun Analytics.trackError(errorType: String, errorMessage: String, context: String) {
    trackEvent(
        AnalyticsEvent(
            name = "error",
            parameters = mapOf(
                "error_type" to errorType,
                "error_message" to errorMessage,
                "context" to context
            )
        )
    )
}

fun Analytics.trackTiming(category: String, variable: String, value: Long, label: String = "") {
    trackEvent(
        AnalyticsEvent(
            name = "timing",
            parameters = mapOf(
                "category" to category,
                "variable" to variable,
                "value" to value,
                "label" to label
            ).filterValues { it.toString().isNotEmpty() }
        )
    )
}