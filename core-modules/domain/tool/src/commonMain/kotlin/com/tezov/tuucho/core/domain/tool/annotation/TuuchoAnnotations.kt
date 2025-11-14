/*
 * Copyright 2017-Present the original author or authors.
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
package com.tezov.tuucho.core.domain.tool.annotation

@Suppress("ktlint:standard:max-line-length")
@RequiresOptIn(
    message = "API marked as @TuuchoInternalApi and is intended for internal framework use only. It may change or be removed in future releases without notice. External usage is strongly discouraged.",
    level = RequiresOptIn.Level.ERROR
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.CONSTRUCTOR,
)
annotation class TuuchoInternalApi

@Suppress("ktlint:standard:max-line-length")
@RequiresOptIn(
    message = "API marked as @TuuchoExperimentalAPI. The current API is actively under development, and may change or be removed without notice. Feedback will help stabilize this API.",
    level = RequiresOptIn.Level.WARNING
)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.CONSTRUCTOR,
)
annotation class TuuchoExperimentalAPI

@Suppress("ktlint:standard:max-line-length")
@RequiresOptIn(
    message = "API marked as @TuuchoDelicateAPI. This API requires careful usage and understanding of its implications. Use with caution as improper usage may lead to unexpected behavior.",
    level = RequiresOptIn.Level.WARNING
)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.CONSTRUCTOR,
)
annotation class TuuchoDelicateAPI
