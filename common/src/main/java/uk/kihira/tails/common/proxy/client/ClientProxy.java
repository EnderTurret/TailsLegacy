/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Zoe Lee (Kihira)
 * Copyright (c) 2020-2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common.proxy.client;

import org.jetbrains.annotations.ApiStatus.Internal;

import uk.kihira.tails.common.proxy.IProxy;

/**
 * <p>The client proxy, buried deep inside a random package so that the class loader will be unable to discover it.</p>
 * <p>If the class loader finds this through something other than {@link IProxy#makeClientProxy()},
 * please dispose of the class loader immediately and get one that is less cursed.</p>
 */
@Internal
public final class ClientProxy implements IProxy {

}