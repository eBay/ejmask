package com.ebay.pmt2.ejmask.core.util;

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

import com.ebay.pmt2.ejmask.api.ILogProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author prakv
 */
class DefaultLogProvider implements ILogProvider {

    private static final Logger LOGGER = Logger.getLogger(DefaultLogProvider.class.getName());

    /**
     * Log a message, as information.
     *
     * @param component as the component trying to log
     * @param flow      as the flow indicator
     * @param message   The string message
     */
    @Override
    public void info(String component, String flow, String message) {
        LOGGER.log(Level.INFO, String.format("%s - %s : %s", component, flow, message));
    }
}
