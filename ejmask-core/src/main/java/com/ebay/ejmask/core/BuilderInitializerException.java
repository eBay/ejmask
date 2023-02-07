package com.ebay.ejmask.core;

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
 * Exception thrown when the EJMash initialization fails.
 *
 * @author prakv
 */
public class BuilderInitializerException extends RuntimeException {

    /**
     * Create new instance of BuilderInitializerException
     *
     * @param message as error message.
     * @param e       as exception case.
     */
    public BuilderInitializerException(String message, Exception e) {
        super(message, e);
    }
}
