/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//pool/src/java/org/apache/commons/pool/KeyedObjectPool.java,v 1.1 2001/04/14 16:40:43 rwaldhoff Exp $
 * $Revision: 1.1 $
 * $Date: 2001/04/14 16:40:43 $
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

package org.apache.commons.pool;

/**
 * A simple "keyed" {@link Object} pooling interface.
 * <p>
 * A keyed pool caches mutiple named instances of {@link Object}s.
 * <p>
 * Example of use:
 * <table border="1" cellspacing="0" cellpadding="3" align="center" bgcolor="#FFFFFF"><tr><td><pre>
 * Object obj = <font color="#0000CC">null</font>;
 * Object key = <font color="#CC0000">"Key"</font>;
 * <font color="#0000CC">try</font> {
 *    obj = keyedPool.borrowObject(key);
 *    <font color="#00CC00">//...use the object...</font>
 * } <font color="#0000CC">catch</font>(Exception e) {
 *    <font color="#00CC00">//...handle any exceptions...</font>
 * } <font color="#0000CC">finally</font> {
 *    <font color="#00CC00">// make sure the object is returned to the pool</font>
 *    <font color="#0000CC">if</font>(<font color="#0000CC">null</font> != obj) {
 *       keyedPool.returnObject(key,obj);
 *    }
 * }</pre></td></tr></table>
 *
 * <p>
 * {@link KeyedObjectPool} implementations <i>may</i> choose to store at most
 * one instance per key value, or may choose to maintain a pool of instances
 * for each key (essentially creating a Map of Pools).
 * </p>
 *
 * @author Rodney Waldhoff
 * @version $Id: KeyedObjectPool.java,v 1.1 2001/04/14 16:40:43 rwaldhoff Exp $
 *
 * @see KeyedPoolableObjectFactory
 * @see KeyedObjectPoolFactory
 * @see ObjectPool
 */
public interface KeyedObjectPool {
    /**
     * Obtain an <tt>Object</tt> from my pool
     * using the specified <i>key</i>.
     * By contract, clients MUST return
     * the borrowed object using
     * {@link #returnObject(java.lang.Object,java.lang.Object) <tt>returnObject</tt>},
     * or a related method as defined in an implementation
     * or sub-interface,
     * using a <i>key</i> that is equivalent to the one used to
     * borrow the <tt>Object</tt> in the first place.
     *
     * @param key the key used to obtain the object
     * @return an <tt>Object</tt> from my pool.
     */
    public abstract Object borrowObject(Object key);

    /**
     * Return an <tt>Object</tt> to my pool.
     * By contract, <i>obj</i> MUST have been obtained
     * using {@link #borrowObject(java.lang.Object) <tt>borrowObject</tt>}
     * or a related method as defined in an implementation
     * or sub-interface,
     * using a <i>key</i> that is equivalent to the one used to
     * borrow the <tt>Object</tt> in the first place.
     *
     * @param key the key used to obtain the object
     * @param obj a {@link #borrowObject(java.lang.Object) borrowed} <tt>Object</tt> to be returned.
     */
    public abstract void returnObject(Object key, Object obj);

    /**
     * Return the number of <tt>Object</tt>s
     * corresponding to the given <i>key</i>
     * currently idle in my pool, or
     * throws {@link UnsupportedOperationException}
     * if this information is not available.
     *
     * @param key the key
     * @return the number of <tt>Object</tt>s corresponding to the given <i>key</i> currently idle in my pool
     * @throws UnsupportedOperationException
     */
    public abstract int numIdle(Object key) throws UnsupportedOperationException;

    /**
     * Return the number of <tt>Object</tt>s
     * currently borrowed from my pool corresponding to the
     * given <i>key</i>, or
     * throws {@link UnsupportedOperationException}
     * if this information is not available.
     *
     * @param key the key
     * @return the number of <tt>Object</tt>s corresponding to the given <i>key</i> currently borrowed in my pool
     * @throws UnsupportedOperationException
     */
    public abstract int numActive(Object key) throws UnsupportedOperationException;

    /**
     * Return the total number of <tt>Object</tt>s
     * currently idle in my pool, or
     * throws {@link UnsupportedOperationException}
     * if this information is not available.
     *
     * @return the total number of <tt>Object</tt>s currently idle in my pool
     * @throws UnsupportedOperationException
     */
    public abstract int numIdle() throws UnsupportedOperationException;

    /**
     * Return the total number of <tt>Object</tt>s
     * current borrowed from my pool, or
     * throws {@link UnsupportedOperationException}
     * if this information is not available.
     *
     * @return the total number of <tt>Object</tt>s currently borrowed in my pool
     * @throws UnsupportedOperationException
     */
    public abstract int numActive() throws UnsupportedOperationException;

    /**
     * Clears my pool, or throws {@link UnsupportedOperationException}
     * if the pool cannot be cleared.
     */
    public abstract void clear() throws UnsupportedOperationException;

    /**
     * Clears the specified pool, or throws {@link UnsupportedOperationException}
     * if the pool cannot be cleared.
     * @param key the key to clear
     */
    public abstract void clear(Object key) throws UnsupportedOperationException;

    /**
     * Close this pool, and free any resources associated with it.
     */
    public abstract void close();

    /**
     * Sets the {@link KeyedPoolableObjectFactory factory} I use
     * to create new instances.
     * @param factory the {@link KeyedPoolableObjectFactory} I use to create new instances.
     */
    public abstract void setFactory(KeyedPoolableObjectFactory factory) throws IllegalStateException, UnsupportedOperationException;
}