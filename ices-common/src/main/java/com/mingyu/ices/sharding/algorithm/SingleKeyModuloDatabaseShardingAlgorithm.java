/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.mingyu.ices.sharding.algorithm;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.SingleKeyDatabaseShardingAlgorithm;

import java.util.Collection;
import java.util.LinkedHashSet;

public final class SingleKeyModuloDatabaseShardingAlgorithm implements SingleKeyDatabaseShardingAlgorithm<String> {

    @Override
    public String doEqualSharding(final Collection<String> availableTargetNames, final ShardingValue<String> shardingValue) {
        String paperId = shardingValue.getValue();

        if (paperId != null && !paperId.equals("")) {
            int hashcode = Math.abs(paperId.hashCode());
            return hashcode % availableTargetNames.size() + "";
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> doInSharding(final Collection<String> availableTargetNames, final ShardingValue<String> shardingValue) {
        int size = availableTargetNames.size();
        Collection<String> result = new LinkedHashSet<>(size);
        Collection<String> values = (Collection<String>) shardingValue.getValues();

        for (String value : values) {
            int hashcode = Math.abs(value.hashCode());
            result.add(hashcode % size + "");
        }
        return result;
    }

    @Override
    public Collection<String> doBetweenSharding(final Collection<String> availableTargetNames, final ShardingValue<String> shardingValue) {
        throw new UnsupportedOperationException();
    }
}
