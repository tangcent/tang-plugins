package com.itangcent.idea.plugin.parse

import com.sun.source.tree.*
import com.sun.source.util.JavacTask
import com.sun.source.util.TreeScanner
import com.sun.tools.javac.api.JavacTrees
import java.io.IOException
import java.util.*
import java.util.function.BinaryOperator
import java.util.function.Predicate
import java.util.stream.Stream
import kotlin.streams.toList

class JavaTree(private val javacTask: JavacTask) {

    private var nodes: Map<CompilationUnitTree, List<Tree>>? = null

    @Throws(IOException::class)
    fun <R, D> accept(visitor: TreeVisitor<R, D>, data: D): List<R> {
        val trees = javacTask.parse()
        val result = ArrayList<R>()
        for (tree in trees) {
            result.add(tree.accept(visitor, data))
        }
        return result
    }

    @Throws(IOException::class)
    fun <R, D> acceptToMap(visitor: TreeVisitor<R, D>, data: D): Map<CompilationUnitTree, R> {
        val trees = javacTask.parse()
        val result = HashMap<CompilationUnitTree, R>()
        for (tree in trees) {
            result[tree] = tree.accept(visitor, data)
        }
        return result
    }

    @Throws(IOException::class)
    fun <R, D> accept(visitor: TreeVisitor<R, D>, data: D, accumulator: BinaryOperator<R>): R? {
        val trees = javacTask.parse()
        var result: R? = null
        var first = true
        for (tree in trees) {
            if (first) {
                result = tree.accept(visitor, data)
                first = false
            } else {
                result = accumulator.apply(result!!, tree.accept(visitor, data))
            }
        }
        return result
    }

    @Throws(IOException::class)
    fun find(predicate: Predicate<Tree>): List<Tree> {
        return nodeStream()
                .filter(predicate)
                .toList()
    }

    @Throws(IOException::class)
    @Suppress("UNCHECKED_CAST")
    fun <T : Tree> find(nodeType: Class<T>): List<T> {
        return nodeStream()
                .filter { nodeType.isInstance(it) }
                .map { tree -> tree as T }
                .toList()
    }

    @Throws(IOException::class)
    fun nodeStream(): Stream<Tree> {
        return allNode().stream()
    }

    @Throws(IOException::class)
    fun allNode(): List<Tree> {
        return nodes().values.stream()
                .reduce { t1, t2 ->
                    t1.union(t2)
                    t1
                }.orElse(ArrayList())
    }

    @Synchronized
    @Throws(IOException::class)
    private fun nodes(): Map<CompilationUnitTree, List<Tree>> {
        return if (this.nodes == null) visitNodes() else this.nodes!!
    }

    @Synchronized
    @Throws(IOException::class)
    private fun visitNodes(): Map<CompilationUnitTree, List<Tree>> {
        if (this.nodes == null) {
            val trees = javacTask.parse()
            val nodes = HashMap<CompilationUnitTree, List<Tree>>()
            val visitor = CollectedTreeVisitor<Void, Void>()
            for (tree in trees) {
                visitor.clear()
                tree.accept(visitor, null)
                nodes[tree] = visitor.nodes()
            }
            this.nodes = nodes
            return nodes
        }
        return this.nodes!!
    }

    internal class CollectedTreeVisitor<R, P> : TreeScanner<R, P>() {

        private val nodes = ArrayList<Tree>(25)

        internal fun clear() {
            nodes.clear()
        }

        protected fun onNode(node: Tree?, p: P?) {
            nodes.add(node!!)
        }

        fun nodes(): List<Tree> {
            return nodes
        }

        override fun visitCompilationUnit(node: CompilationUnitTree, p: P): R {
            onNode(node, p)
            return super.visitCompilationUnit(node, p)
        }

        override fun visitImport(node: ImportTree, p: P): R {
            onNode(node, p)
            return super.visitImport(node, p)
        }

        override fun visitClass(node: ClassTree, p: P): R {
            onNode(node, p)
            return super.visitClass(node, p)
        }

        override fun visitMethod(node: MethodTree, p: P): R {
            onNode(node, p)
            return super.visitMethod(node, p)
        }

        override fun visitVariable(node: VariableTree, p: P): R {
            onNode(node, p)
            return super.visitVariable(node, p)
        }

        override fun visitEmptyStatement(node: EmptyStatementTree?, p: P?): R {
            onNode(node, p)
            return super.visitEmptyStatement(node, p)
        }

        override fun visitBlock(node: BlockTree, p: P): R {
            onNode(node, p)
            return super.visitBlock(node, p)
        }

        override fun visitDoWhileLoop(node: DoWhileLoopTree, p: P): R {
            onNode(node, p)
            return super.visitDoWhileLoop(node, p)
        }

        override fun visitWhileLoop(node: WhileLoopTree, p: P): R {
            onNode(node, p)
            return super.visitWhileLoop(node, p)
        }

        override fun visitForLoop(node: ForLoopTree, p: P): R {
            onNode(node, p)
            return super.visitForLoop(node, p)
        }

        override fun visitEnhancedForLoop(node: EnhancedForLoopTree, p: P): R {
            onNode(node, p)
            return super.visitEnhancedForLoop(node, p)
        }

        override fun visitLabeledStatement(node: LabeledStatementTree, p: P): R {
            onNode(node, p)
            return super.visitLabeledStatement(node, p)
        }

        override fun visitSwitch(node: SwitchTree, p: P): R {
            onNode(node, p)
            return super.visitSwitch(node, p)
        }

        override fun visitCase(node: CaseTree, p: P): R {
            onNode(node, p)
            return super.visitCase(node, p)
        }

        override fun visitSynchronized(node: SynchronizedTree, p: P): R {
            onNode(node, p)
            return super.visitSynchronized(node, p)
        }

        override fun visitTry(node: TryTree, p: P): R {
            onNode(node, p)
            return super.visitTry(node, p)
        }

        override fun visitCatch(node: CatchTree, p: P): R {
            onNode(node, p)
            return super.visitCatch(node, p)
        }

        override fun visitConditionalExpression(node: ConditionalExpressionTree, p: P): R {
            onNode(node, p)
            return super.visitConditionalExpression(node, p)
        }

        override fun visitIf(node: IfTree, p: P): R {
            onNode(node, p)
            return super.visitIf(node, p)
        }

        override fun visitExpressionStatement(node: ExpressionStatementTree, p: P): R {
            onNode(node, p)
            return super.visitExpressionStatement(node, p)
        }

        override fun visitBreak(node: BreakTree?, p: P?): R {
            onNode(node, p)
            return super.visitBreak(node, p)
        }

        override fun visitContinue(node: ContinueTree?, p: P?): R {
            onNode(node, p)
            return super.visitContinue(node, p)
        }

        override fun visitReturn(node: ReturnTree, p: P): R {
            onNode(node, p)
            return super.visitReturn(node, p)
        }

        override fun visitThrow(node: ThrowTree, p: P): R {
            onNode(node, p)
            return super.visitThrow(node, p)
        }

        override fun visitAssert(node: AssertTree, p: P): R {
            onNode(node, p)
            return super.visitAssert(node, p)
        }

        override fun visitMethodInvocation(node: MethodInvocationTree, p: P): R {
            onNode(node, p)
            return super.visitMethodInvocation(node, p)
        }

        override fun visitNewClass(node: NewClassTree, p: P): R {
            onNode(node, p)
            return super.visitNewClass(node, p)
        }

        override fun visitNewArray(node: NewArrayTree, p: P): R {
            onNode(node, p)
            return super.visitNewArray(node, p)
        }

        override fun visitLambdaExpression(node: LambdaExpressionTree, p: P): R {
            onNode(node, p)
            return super.visitLambdaExpression(node, p)
        }

        override fun visitParenthesized(node: ParenthesizedTree, p: P): R {
            onNode(node, p)
            return super.visitParenthesized(node, p)
        }

        override fun visitAssignment(node: AssignmentTree, p: P): R {
            onNode(node, p)
            return super.visitAssignment(node, p)
        }

        override fun visitCompoundAssignment(node: CompoundAssignmentTree, p: P): R {
            onNode(node, p)
            return super.visitCompoundAssignment(node, p)
        }

        override fun visitUnary(node: UnaryTree, p: P): R {
            onNode(node, p)
            return super.visitUnary(node, p)
        }

        override fun visitBinary(node: BinaryTree, p: P): R {
            onNode(node, p)
            return super.visitBinary(node, p)
        }

        override fun visitTypeCast(node: TypeCastTree, p: P): R {
            onNode(node, p)
            return super.visitTypeCast(node, p)
        }

        override fun visitInstanceOf(node: InstanceOfTree, p: P): R {
            onNode(node, p)
            return super.visitInstanceOf(node, p)
        }

        override fun visitArrayAccess(node: ArrayAccessTree, p: P): R {
            onNode(node, p)
            return super.visitArrayAccess(node, p)
        }

        override fun visitMemberSelect(node: MemberSelectTree, p: P): R {
            onNode(node, p)
            return super.visitMemberSelect(node, p)
        }

        override fun visitMemberReference(node: MemberReferenceTree, p: P): R {
            onNode(node, p)
            return super.visitMemberReference(node, p)
        }

        override fun visitIdentifier(node: IdentifierTree?, p: P?): R {
            onNode(node, p)
            return super.visitIdentifier(node, p)
        }

        override fun visitLiteral(node: LiteralTree?, p: P?): R {
            onNode(node, p)
            return super.visitLiteral(node, p)
        }

        override fun visitPrimitiveType(node: PrimitiveTypeTree?, p: P?): R {
            onNode(node, p)
            return super.visitPrimitiveType(node, p)
        }

        override fun visitArrayType(node: ArrayTypeTree, p: P): R {
            onNode(node, p)
            return super.visitArrayType(node, p)
        }

        override fun visitParameterizedType(node: ParameterizedTypeTree, p: P): R {
            onNode(node, p)
            return super.visitParameterizedType(node, p)
        }

        override fun visitUnionType(node: UnionTypeTree, p: P): R {
            onNode(node, p)
            return super.visitUnionType(node, p)
        }

        override fun visitIntersectionType(node: IntersectionTypeTree, p: P): R {
            onNode(node, p)
            return super.visitIntersectionType(node, p)
        }

        override fun visitTypeParameter(node: TypeParameterTree, p: P): R {
            onNode(node, p)
            return super.visitTypeParameter(node, p)
        }

        override fun visitWildcard(node: WildcardTree, p: P): R {
            onNode(node, p)
            return super.visitWildcard(node, p)
        }

        override fun visitModifiers(node: ModifiersTree, p: P): R {
            onNode(node, p)
            return super.visitModifiers(node, p)
        }

        override fun visitAnnotation(node: AnnotationTree, p: P): R {
            onNode(node, p)
            return super.visitAnnotation(node, p)
        }

        override fun visitAnnotatedType(node: AnnotatedTypeTree, p: P): R {
            onNode(node, p)
            return super.visitAnnotatedType(node, p)
        }

        override fun visitOther(node: Tree?, p: P?): R {
            onNode(node, p)
            return super.visitOther(node, p)
        }

        override fun visitErroneous(node: ErroneousTree?, p: P?): R {
            onNode(node, p)
            return super.visitErroneous(node, p)
        }
    }

    fun getStartPosition(tree: Tree): Long {
        val compilationUnitTree = findCompilationUnitTree(tree)
        val javacTrees = JavacTrees.instance(javacTask)
        return javacTrees.sourcePositions.getStartPosition(compilationUnitTree, tree)
    }

    fun getEndPosition(tree: Tree): Long {
        val compilationUnitTree = findCompilationUnitTree(tree)
        val javacTrees = JavacTrees.instance(javacTask)
        return javacTrees.sourcePositions.getEndPosition(compilationUnitTree, tree)
    }

    fun getPosition(tree: Tree): Position {
        val compilationUnitTree = findCompilationUnitTree(tree)
        val javacTrees = JavacTrees.instance(javacTask)
        val positions = javacTrees.sourcePositions
        return Position.of(positions.getStartPosition(compilationUnitTree, tree), positions.getEndPosition(compilationUnitTree, tree))
    }

    fun findCompilationUnitTree(tree: Tree): CompilationUnitTree? {

        if (nodes != null) {
            for (compilationUnitTree in nodes!!.keys) {
                if (nodes!![compilationUnitTree]?.contains(tree)!!) {
                    return compilationUnitTree
                }
            }
        }
        return null
    }
}
