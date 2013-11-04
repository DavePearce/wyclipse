package wyclipse.ui.dialogs;

/**
 * <p>
 * Responsible for allowing the user to select an existing folder relative from
 * a project root, or to add a new one. This is used in the
 * <code>NewWhileyProjectWizard</code> and the
 * <code>WhileyPathPropertyPage</code> for configuring source and output
 * folders. This dialog will display existing folders relative to the given
 * project.
 * </p>
 * 
 * <b>Note:</b> that this dialog does not actually create any new folders,
 * although it gives the appearance of doing so. This is because the folders are
 * only created when the given "transaction" is completed. That is, when the
 * used selects finish or apply on the <code>NewWhileyProjectWizard</code> or
 * <code>WhileyPathPropertyPage</code>. Thus, in the case that the user selects
 * "cancel", there is actually nothing to undo.
 * 
 * @author David J. Pearce
 * 
 */
public class FolderSelectionDialog {

}
