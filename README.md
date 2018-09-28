#tang-plugins

## Install

- [Download](https://github.com/tangcent/tang-plugins/plugin/idea-plugin.jar) the plugin jar and select "Install Plugin From Disk" in IntelliJ's plugin preferences.

## build

```bash
 ./plugin-script/build_plugin.sh
```

##actions

```xml
    <actions>
        <group id="TmXMenu" text="Tm" description="Tm XMenu">
            <add-to-group group-id="MainMenu" anchor="last"/>
            <action id="TmMenu.Fields"
                    class="com.itangcent.idea.plugin.actions.FieldsAction"
                    text="GenerateFields" description="Tm plugin for generate fields"/>
            <action id="com.itangcent.idea.plugin.actions.FormatAction"
                    class="com.itangcent.idea.plugin.actions.FormatAction" text="TFormat" description="TFormat"/>
            <action id="com.itangcent.idea.plugin.actions.FieldsJsonAction"
                    class="com.itangcent.idea.plugin.actions.FieldsJsonAction" text="ToJson" description="ToJson"/>
            <action id="com.itangcent.idea.plugin.actions.CopyRightAction"
                    class="com.itangcent.idea.plugin.actions.CopyRightAction" text="CopyRight" description="CopyRight"/>
            <action id="com.itangcent.idea.plugin.actions.ProjectAction"
                    class="com.itangcent.idea.plugin.actions.ProjectAction" text="Project" description="Project"/>
            <action id="com.itangcent.idea.plugin.actions.SettingAction"
                    class="com.itangcent.idea.plugin.actions.SettingAction" text="Setting" description="Setting"/>
            <action id="com.itangcent.idea.plugin.actions.ClipboardAction"
                    class="com.itangcent.idea.plugin.actions.ClipboardAction" text="Clipboard" description="Clipboard"/>
        </group>

        <group id="TmGenerateExt" text="_TmGenerateExt" description="Tm XMenu">
            <add-to-group group-id="GenerateGroup" anchor="last"/>
            <action id="GenerateGroup.GenerateFields"
                    class="com.itangcent.idea.plugin.actions.FieldsAction"
                    text="_Generate Fields" description="T_Generate Fields">
            </action>
            <action id="com.itangcent.idea.plugin.actions.ModificationTagAction"
                    class="com.itangcent.idea.plugin.actions.ModificationTagAction" text="ModificationTag"
                    description="ModificationTag"/>
            <action id="com.itangcent.idea.plugin.actions.InnerClassAction"
                    class="com.itangcent.idea.plugin.actions.InnerClassAction" text="InnerClass"
                    description="InnerClass"/>
            <action id="com.itangcent.idea.plugin.actions.SaveToClipboardAction"
                    class="com.itangcent.idea.plugin.actions.SaveToClipboardAction" text="SaveToClipboard"
                    description="Save to clipboard"/>
        </group>

        <action id="com.itangcent.idea.plugin.actions.SaveToClipboardAction"
                class="com.itangcent.idea.plugin.actions.SaveToClipboardAction" text="SaveToClipboard"
                description="Save to clipboard">
            <add-to-group group-id="EditMenu" anchor="first"/>
            <add-to-group group-id="EditorPopupMenu" anchor="first"/>
            <keyboard-shortcut keymap="$default" first-keystroke="alt S"/>
        </action>
    </actions>
```