/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.pool2.pool407;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;

public final class Pool407 extends GenericObjectPool<Pool407Fixture, RuntimeException> {

    public Pool407(final Pool407Fixture fixture) {
        super(new Pool407NormalFactory(fixture), new Pool407Config());
    }

    public Pool407(final BasePooledObjectFactory<Pool407Fixture, RuntimeException> factory) {
        super(factory, new Pool407Config());
    }
}