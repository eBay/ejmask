package com.ebay.pmt2.ejmask.api;

/**
 * Copyright (c) 2023 eBay Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Defines a Log provider
 *
 * @author prakv
 */
public interface ILogProvider {

    /**
     * Log a message, as information.
     *
     * @param component as the component trying to log
     * @param flow      as the flow indicator
     * @param message   The string message
     */
    void info(String component, String flow, String message);

    /**
     * Log a message, as warning.
     *
     * @param component as the component trying to log
     * @param flow      as the flow indicator
     * @param message   The string message
     */
    default void warning(String component, String flow, String message) {
        this.info(component, flow, message);
    }

    /**
     * Log a message, as error.
     *
     * @param component as the component trying to log
     * @param flow      as the flow indicator
     * @param message   The string message
     */
    default void error(String component, String flow, String message) {
        this.info(component, flow, message);
    }
}
