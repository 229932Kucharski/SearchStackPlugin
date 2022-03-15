package org.intellij.sdk.action;

import com.intellij.ide.BrowserUtil;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class SearchAction extends AnAction {

    public SearchAction() {
        super();
    }

    public SearchAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        String selectedText = null;
        String languageTag = "";
        if (file != null) {
            Language lang = e.getData(CommonDataKeys.PSI_FILE).getLanguage();
            languageTag = "+[" + lang.getDisplayName().toLowerCase() + "]";

            Caret editor = e.getRequiredData(CommonDataKeys.CARET);
            CaretModel caretModel = editor.getCaretModel();
            selectedText = caretModel.getCurrentCaret().getSelectedText();
        }
        if (selectedText == null) {
            try {
                selectedText = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException unsupportedFlavorException) {
                unsupportedFlavorException.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        String query = selectedText.replace(' ', '+') + languageTag;
        BrowserUtil.browse("https://stackoverflow.com/search?q=" + query);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Caret editor = e.getRequiredData(CommonDataKeys.CARET);
        CaretModel caretModel = editor.getCaretModel();
        boolean available = Toolkit.getDefaultToolkit().getSystemClipboard().isDataFlavorAvailable(DataFlavor.stringFlavor);
        e.getPresentation().setEnabledAndVisible(caretModel.getCurrentCaret().hasSelection()||available);
    }
}
