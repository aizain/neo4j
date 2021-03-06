/*
 * Copyright (c) "Neo4j"
 * Neo4j Sweden AB [http://neo4j.com]
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
package org.neo4j.cypher.internal.rewriting.rewriters

import org.neo4j.cypher.internal.expressions.NodePattern
import org.neo4j.cypher.internal.expressions.RelationshipPattern
import org.neo4j.cypher.internal.expressions.ShortestPathExpression
import org.neo4j.cypher.internal.expressions.Variable
import org.neo4j.cypher.internal.util.NodeNameGenerator
import org.neo4j.cypher.internal.util.RelNameGenerator
import org.neo4j.cypher.internal.util.Rewriter
import org.neo4j.cypher.internal.util.bottomUp

case object nameAllPatternElements extends Rewriter {

  override def apply(in: AnyRef): AnyRef = namingRewriter.apply(in)

  private val namingRewriter: Rewriter = bottomUp(Rewriter.lift {
    case pattern: NodePattern if pattern.variable.isEmpty =>
      val syntheticName = NodeNameGenerator.name(pattern.position.bumped())
      pattern.copy(variable = Some(Variable(syntheticName)(pattern.position)))(pattern.position)

    case pattern: RelationshipPattern if pattern.variable.isEmpty  =>
      val syntheticName = RelNameGenerator.name(pattern.position.bumped())
      pattern.copy(variable = Some(Variable(syntheticName)(pattern.position)))(pattern.position)
  }, stopper = {
    case _: ShortestPathExpression => true
    case _ => false
  })
}
