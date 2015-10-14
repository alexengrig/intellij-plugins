package org.jetbrains.plugins.ruby.motion.symbols;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.Module;
import com.jetbrains.cidr.CocoaDocumentationManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.motion.bridgesupport.Constant;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.RTypedSyntheticSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;

import javax.swing.*;

/**
 * @author Dennis.Ushakov
 */
public class ConstantSymbol extends RTypedSyntheticSymbol implements MotionSymbol {
  @NotNull private final Module myModule;
  @NotNull private final Constant myConstant;

  public ConstantSymbol(@NotNull Module module,
                        @NotNull Constant constant,
                        @Nullable String name,
                        @NotNull RType returnType) {
    super(module.getProject(), name, Type.CONSTANT, null, returnType, 0);
    myModule = module;
    myConstant = constant;
  }

  @NotNull
  @Override
  public Module getModule() {
    return myModule;
  }

  @Nullable
  @Override
  public Icon getExplicitIcon() {
    return myConstant instanceof org.jetbrains.plugins.ruby.motion.bridgesupport.Enum ? AllIcons.Nodes.Enum : null;
  }

  @Override
  public CocoaDocumentationManager.DocTokenType getInfoType() {
    return myConstant instanceof org.jetbrains.plugins.ruby.motion.bridgesupport.Enum ?
           CocoaDocumentationManager.DocTokenType.ENUM_CONSTANT :
           CocoaDocumentationManager.DocTokenType.CLASS_CONSTANT;
  }

  @Override
  public String getInfoName() {
    final String name = getName();
    assert name != null;
    return name;
  }

  @NotNull
  public Constant getConstant() {
    return myConstant;
  }
}
