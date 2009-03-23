/**
 *  Copyright (C) 2006 Orbeon, Inc.
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the
 *  GNU Lesser General Public License as published by the Free Software Foundation; either version
 *  2.1 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  The full text of the license is available at http://www.gnu.org/copyleft/lesser.html
 */
package org.orbeon.oxf.xforms.action.actions;

import org.dom4j.Element;
import org.orbeon.oxf.pipeline.api.PipelineContext;
import org.orbeon.oxf.xforms.XFormsContainingDocument;
import org.orbeon.oxf.xforms.action.XFormsAction;
import org.orbeon.oxf.xforms.action.XFormsActionInterpreter;
import org.orbeon.oxf.xforms.control.controls.XXFormsDialogControl;
import org.orbeon.oxf.xforms.event.XFormsEvent;
import org.orbeon.oxf.xforms.event.XFormsEventObserver;
import org.orbeon.oxf.xforms.event.XFormsEventTarget;
import org.orbeon.oxf.xforms.event.events.XXFormsDialogCloseEvent;
import org.orbeon.oxf.xforms.processor.XFormsServer;
import org.orbeon.saxon.om.Item;

/**
 * Extension xxforms:hide action.
 */
public class XXFormsHideAction extends XFormsAction {

    public void execute(XFormsActionInterpreter actionInterpreter, PipelineContext pipelineContext, String targetId,
                        XFormsEventObserver eventObserver, Element actionElement,
                        boolean hasOverriddenContext, Item overriddenContext) {

        final XFormsContainingDocument containingDocument = actionInterpreter.getContainingDocument();

        // Resolve attribute as AVTs
        final String dialogId = resolveAVT(actionInterpreter, pipelineContext, actionElement, "dialog", true);
        if (dialogId == null) {
            // TODO: Should we try to find the dialog containing the action, of the dialog containing the observer or the target causing this event?
        }

        if (dialogId != null) {
            // Dispatch xxforms-dialog-close event to dialog
            // TODO: use container.getObjectByEffectiveId() once XFormsContainer is able to have local controls
            final Object controlObject = resolveEffectiveControl(actionInterpreter, pipelineContext, eventObserver.getEffectiveId(), dialogId, actionElement);
            if (controlObject instanceof XXFormsDialogControl) {
                final XFormsEventTarget eventTarget = (XFormsEventTarget) controlObject;
                final XFormsEvent newEvent = new XXFormsDialogCloseEvent(eventTarget);
                addContextAttributes(actionInterpreter, pipelineContext, actionElement, newEvent);
                eventTarget.getContainer(containingDocument).dispatchEvent(pipelineContext, newEvent);
            } else {
                if (XFormsServer.logger.isDebugEnabled())
                    containingDocument.logDebug("xxforms:hide", "dialog does not refer to an existing xxforms:dialog element, ignoring action",
                            new String[]{"dialog id", dialogId});
            }
        }
    }
}
