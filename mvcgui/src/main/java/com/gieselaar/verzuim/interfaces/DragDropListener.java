package com.gieselaar.verzuim.interfaces;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.List;

import com.gieselaar.verzuim.utils.ExceptionLogger;

public class DragDropListener implements DropTargetListener {

	private DragDropNotification dragdropNotifier;

	@Override
    public void drop(DropTargetDropEvent event) {

        // Accept copy drops
        event.acceptDrop(DnDConstants.ACTION_COPY);

        // Get the transfer which can provide the dropped item data
        Transferable transferable = event.getTransferable();

        // Get the data formats of the dropped item
        DataFlavor[] flavors = transferable.getTransferDataFlavors();

        // Loop through the flavors
        for (DataFlavor flavor : flavors) {

            try {

                // If the drop items are files
                if (flavor.isFlavorJavaFileListType()) {

                    // Get all of the dropped files
                    @SuppressWarnings("unchecked")
					List<File> files = (List<File>) transferable.getTransferData(flavor);

                    // Loop them through
                    for (File file : files) {

                    	if (dragdropNotifier != null)
                    		dragdropNotifier.fileDropped(file);

                    }

                }

            } catch (Exception e) {

            	ExceptionLogger.ProcessException(e,null);

            }
        }

        // Inform that the drop is complete
        event.dropComplete(true);

    }

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
	}

	public DragDropNotification getDragdropListener() {
		return dragdropNotifier;
	}

	public void setDragDropNotification(DragDropNotification notifier) {
		this.dragdropNotifier = notifier;
	}
}
