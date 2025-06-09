package com.pointlessapps.granite.markdown.renderer.models

import com.pointlessapps.granite.markdown.renderer.NodeProcessor

enum class ChildrenProcessing {
    /**
     * Makes the [NodeProcessor] process all of the children of the node.
     */
    PROCESS_CHILDREN,

    /**
     * Skips the processing of the parent, but still makes its way down the tree
     * processing the children.
     */
    SKIP_PARENT,

    /**
     * Skips the processing of the children altogether.
     */
    SKIP
}
