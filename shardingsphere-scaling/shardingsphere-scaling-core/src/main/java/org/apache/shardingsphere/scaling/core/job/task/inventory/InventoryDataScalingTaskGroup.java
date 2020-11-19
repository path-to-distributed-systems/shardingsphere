/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.scaling.core.job.task.inventory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.scaling.core.execute.executor.AbstractShardingScalingExecutor;
import org.apache.shardingsphere.scaling.core.job.SyncProgress;
import org.apache.shardingsphere.scaling.core.job.position.InventoryPosition;
import org.apache.shardingsphere.scaling.core.job.position.PositionManager;
import org.apache.shardingsphere.scaling.core.job.task.ScalingTask;

import java.util.Collection;

/**
 * Inventory data sync task group.
 */
@Slf4j
@Getter
public final class InventoryDataScalingTaskGroup extends AbstractShardingScalingExecutor<InventoryPosition> implements ScalingTask<InventoryPosition> {
    
    private final Collection<ScalingTask<InventoryPosition>> scalingTasks;
    
    public InventoryDataScalingTaskGroup(final Collection<ScalingTask<InventoryPosition>> inventoryDataScalingTasks) {
        scalingTasks = inventoryDataScalingTasks;
    }
    
    @Override
    public void start() {
        super.start();
        for (ScalingTask<InventoryPosition> each : scalingTasks) {
            PositionManager<InventoryPosition> positionManager = each.getPositionManager();
            if (null != positionManager && null != positionManager.getPosition() && !positionManager.getPosition().isFinished()) {
                each.start();
            }
        }
    }
    
    @Override
    public void stop() {
        for (ScalingTask<InventoryPosition> each : scalingTasks) {
            each.stop();
        }
    }
    
    @Override
    public SyncProgress getProgress() {
        InventoryDataSyncTaskProgressGroup result = new InventoryDataSyncTaskProgressGroup();
        for (ScalingTask<InventoryPosition> each : scalingTasks) {
            result.addSyncProgress(each.getProgress());
        }
        return result;
    }
}
