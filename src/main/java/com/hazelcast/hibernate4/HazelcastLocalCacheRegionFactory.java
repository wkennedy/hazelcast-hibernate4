/*
 * Copyright (c) 2008-2012, Hazel Bilisim Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.hibernate4;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.hibernate4.local.CleanupService;
import com.hazelcast.hibernate4.local.LocalRegionCache;
import com.hazelcast.hibernate4.region.HazelcastCollectionRegion;
import com.hazelcast.hibernate4.region.HazelcastEntityRegion;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cfg.Settings;

import java.util.Properties;

public class HazelcastLocalCacheRegionFactory extends AbstractHazelcastCacheRegionFactory implements RegionFactory {

    private CleanupService cleanupService;

    public HazelcastLocalCacheRegionFactory() {
    }

    public HazelcastLocalCacheRegionFactory(final HazelcastInstance instance) {
        super(instance);
    }

    public HazelcastLocalCacheRegionFactory(final Properties properties) {
        super(properties);
    }

    public CollectionRegion buildCollectionRegion(final String regionName, final Properties properties,
                                                  final CacheDataDescription metadata) throws CacheException {
        final HazelcastCollectionRegion<LocalRegionCache> region = new HazelcastCollectionRegion<LocalRegionCache>(instance,
                regionName, properties, metadata, new LocalRegionCache(regionName, instance, metadata));
        cleanupService.registerCache(region.getCache());
        return region;
    }

    public EntityRegion buildEntityRegion(final String regionName, final Properties properties,
                                          final CacheDataDescription metadata) throws CacheException {
        final HazelcastEntityRegion<LocalRegionCache> region = new HazelcastEntityRegion<LocalRegionCache>(instance,
                regionName, properties, metadata, new LocalRegionCache(regionName, instance, metadata));
        cleanupService.registerCache(region.getCache());
        return region;
    }

    @Override
    public void start(final Settings settings, final Properties properties) throws CacheException {
        super.start(settings, properties);
        cleanupService = new CleanupService(instance.getName());
    }

    @Override
    public void stop() {
        cleanupService.stop();
        super.stop();
    }
}