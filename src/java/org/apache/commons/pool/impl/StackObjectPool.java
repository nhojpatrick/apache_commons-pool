/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//pool/src/java/org/apache/commons/pool/impl/StackObjectPool.java,v 1.1 2001/04/14 16:41:56 rwaldhoff Exp $
 * $Revision: 1.1 $
 * $Date: 2001/04/14 16:41:56 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.pool.impl;

import org.apache.commons.pool.*;
import java.util.Stack;
import java.util.NoSuchElementException;
import java.util.Enumeration;

/**
 * A simple, {@link java.util.Stack Stack}-based {@link ObjectPool} implementation.
 * <p>
 * Given a {@link PoolableObjectFactory}, this class will maintain
 * a simple pool of instances.  A finite number of "sleeping"
 * or idle instances is enforced, but when the pool is
 * empty, new instances are created to support the new load.
 * Hence this class places no limit on the number of "active"
 * instances created by the pool, but is quite useful for
 * re-using <tt>Object</tt>s without introducing
 * artificial limits.
 *
 * @author Rodney Waldhoff
 * @version $Id: StackObjectPool.java,v 1.1 2001/04/14 16:41:56 rwaldhoff Exp $
 */
public class StackObjectPool implements ObjectPool {
    /**
     * Create a new pool using
     * no factory. Clients must first populate the pool
     * using {@link #returnObject(java.lang.Object)}
     * before they can be {@link #borrowObject borrowed}.
     */
    public StackObjectPool() {
        this((PoolableObjectFactory)null,DEFAULT_MAX_SLEEPING,DEFAULT_INIT_SLEEPING_CAPACITY);
    }

    /**
     * Create a new pool using
     * no factory. Clients must first populate the pool
     * using {@link #returnObject(java.lang.Object)}
     * before they can be {@link #borrowObject borrowed}.
     */
    public StackObjectPool(int max) {
        this((PoolableObjectFactory)null,max,DEFAULT_INIT_SLEEPING_CAPACITY);
    }

    /**
     * Create a new pool using
     * no factory. Clients must first populate the pool
     * using {@link #returnObject(java.lang.Object)}
     * before they can be {@link #borrowObject borrowed}.
     */
    public StackObjectPool(int max, int init) {
        this((PoolableObjectFactory)null,max,init);
    }

    /**
     * Create a new <tt>StackObjectPool</tt> using
     * the specified <i>factory</i> to create new instances.
     *
     * @param factory the {@link PoolableObjectFactory} used to populate the pool
     */
    public StackObjectPool(PoolableObjectFactory factory) {
        this(factory,DEFAULT_MAX_SLEEPING,DEFAULT_INIT_SLEEPING_CAPACITY);
    }

    /**
     * Create a new <tt>SimpleObjectPool</tt> using
     * the specified <i>factory</i> to create new instances,
     * capping the number of "sleeping" instances to <i>max</i>
     *
     * @param factory the {@link PoolableObjectFactory} used to populate the pool
     * @param max cap on the number of "sleeping" instances in the pool
     */
    public StackObjectPool(PoolableObjectFactory factory, int max) {
        this(factory,max,DEFAULT_INIT_SLEEPING_CAPACITY);
    }

    /**
     * Create a new <tt>SimpleObjectPool</tt> using
     * the specified <i>factory</i> to create new instances,
     * capping the number of "sleeping" instances to <i>max</i>,
     * and initially allocating a container capable of containing
     * at least <i>init</i> instances.
     *
     * @param factory the {@link PoolableObjectFactory} used to populate the pool
     * @param max cap on the number of "sleeping" instances in the pool
     * @param init initial size of the pool (this specifies the size of the container,
     *             it does not cause the pool to be pre-populated.)
     */
    public StackObjectPool(PoolableObjectFactory factory, int max, int init) {
        _factory = factory;
        _maxSleeping = (max < 0 ? DEFAULT_MAX_SLEEPING : max);
        int initcapacity = (init < 1 ? DEFAULT_INIT_SLEEPING_CAPACITY : init);
        _pool = new Stack();
        _pool.ensureCapacity( initcapacity > _maxSleeping ? _maxSleeping : initcapacity);
    }

    public synchronized Object borrowObject() {
        Object obj = null;
        try {
            obj = _pool.pop();
        } catch(Exception e) {
            if(null == _factory) {
                throw new NoSuchElementException();
            } else {
                obj = _factory.makeObject();
            }
        }
        if(null != _factory && null != obj) {
            _factory.activateObject(obj);
        }
        _numActive++;
        return obj;
    }

    public synchronized void returnObject(Object obj) {
        _numActive--;
        if(null == _factory || _factory.validateObject(obj)) {
            if(null != _factory) {
                try {
                    _factory.passivateObject(obj);
                } catch(Exception e) {
                    _factory.destroyObject(obj);
                    return;
                }
            }
            if(_pool.size() < _maxSleeping) {
                _pool.push(obj);
            } else {
                if(null != _factory) {
                    _factory.destroyObject(obj);
                }
            }
        } else {
            if(null != _factory) {
                _factory.destroyObject(obj);
            }
        }
    }

    public int numIdle() {
        return _pool.size();
    }

    public int numActive() {
        return _numActive;
    }

    public synchronized void clear() throws UnsupportedOperationException {
        if(null != _factory) {
            Enumeration enum = _pool.elements();
            while(enum.hasMoreElements()) {
                _factory.destroyObject(enum.nextElement());
            }
        }
        _pool.clear();
    }

    synchronized public void close() {
        clear();
        _pool = null;
        _factory = null;
    }

    synchronized public void setFactory(PoolableObjectFactory factory) throws IllegalStateException, UnsupportedOperationException {
        if(0 < numActive()) {
            throw new IllegalStateException("Objects are already active");
        } else {
            clear();
            _factory = factory;
        }
    }

    /** The default cap on the number of "sleeping" instances in the pool. */
    protected static final int DEFAULT_MAX_SLEEPING  = 8;

    /**
     * The default initial size of the pool
     * (this specifies the size of the container, it does not
     * cause the pool to be pre-populated.)
     */
    protected static final int DEFAULT_INIT_SLEEPING_CAPACITY = 4;

    /** My pool. */
    protected Stack _pool = null;

    /** My {@link PoolableObjectFactory}. */
    protected PoolableObjectFactory _factory = null;

    /** The cap on the number of "sleeping" instances in the pool. */
    protected int _maxSleeping = DEFAULT_MAX_SLEEPING;

    protected int _numActive = 0;
}