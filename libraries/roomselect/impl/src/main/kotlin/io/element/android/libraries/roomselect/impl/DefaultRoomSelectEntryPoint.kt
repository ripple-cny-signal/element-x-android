/*
 * Copyright (c) 2023 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.element.android.libraries.roomselect.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.squareup.anvil.annotations.ContributesBinding
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.roomselect.api.RoomSelectEntryPoint
import javax.inject.Inject

@ContributesBinding(SessionScope::class)
class DefaultRoomSelectEntryPoint @Inject constructor() : RoomSelectEntryPoint {
    override fun nodeBuilder(parentNode: Node, buildContext: BuildContext): RoomSelectEntryPoint.NodeBuilder {
        val plugins = ArrayList<Plugin>()

        return object : RoomSelectEntryPoint.NodeBuilder {
            override fun params(params: RoomSelectEntryPoint.Params): RoomSelectEntryPoint.NodeBuilder {
                plugins += RoomSelectNode.Inputs(mode = params.mode)
                return this
            }

            override fun callback(callback: RoomSelectEntryPoint.Callback): RoomSelectEntryPoint.NodeBuilder {
                plugins += callback
                return this
            }

            override fun build(): Node {
                return parentNode.createNode<RoomSelectNode>(buildContext, plugins)
            }
        }
    }
}
