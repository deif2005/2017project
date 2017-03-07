/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
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
 * </p>
 */

package com.mingyu.ices.sharding.algorithm;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.SingleKeyTableShardingAlgorithm;

import java.util.Collection;
import java.util.LinkedHashSet;

public final class SingleKeyModuloTableShardingAlgorithm implements SingleKeyTableShardingAlgorithm<String> {
    
    @Override
    public String doEqualSharding(final Collection<String> availableTargetNames, final ShardingValue<String> shardingValue) {
        String shardingId = shardingValue.getValue();

        if (shardingId != null && !shardingId.equals("")) {
            int size = availableTargetNames.size();
            String[] tableList = availableTargetNames.toArray(new String[size]);

            int hashcode = Math.abs(shardingId.hashCode());
            int index = hashcode % size;
            return tableList[index];
        }

        throw new UnsupportedOperationException();
    }
    
    @Override
    public Collection<String> doInSharding(final Collection<String> availableTargetNames, final ShardingValue<String> shardingValue) {
        int size = availableTargetNames.size();
        String[] tableList = availableTargetNames.toArray(new String[size]);

        Collection<String> result = new LinkedHashSet<>(availableTargetNames.size());
        Collection<String> values = shardingValue.getValues();

        for (String value : values) {
            int hashcode = Math.abs(value.hashCode());
            int index = hashcode % size;
            result.add(tableList[index]);
        }
        return result;
    }
    
    @Override
    public Collection<String> doBetweenSharding(final Collection<String> availableTargetNames, final ShardingValue<String> shardingValue) {
        throw new UnsupportedOperationException();
    }
}
