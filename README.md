# Android Support library - preference v7 bugfix

[ ![Download](https://api.bintray.com/packages/gericop/maven/com.takisoft.fix/images/download.svg) ](https://bintray.com/gericop/maven/com.takisoft.fix/_latestVersion)

## How to use the library?
### 1. Add gradle dependency
First, **remove** the unnecessary lines of preference-v7 and preference-v14 from your gradle file as the bugfix contains both of them:
```gradle
compile 'com.android.support:preference-v7:26.0.0-beta2'
compile 'com.android.support:preference-v14:26.0.0-beta2'
```
And **add** this single line to your gradle file:
```gradle
compile 'com.takisoft.fix:preference-v7:26.0.0.0-beta2'
```
> Notice the versioning: the first three numbers are *always* the same as the latest official library while the last number is for own updates. I try to keep it up-to-date but if, for whatever reasons, I wouldn't notice the new support library versions, just issue a ticket.

### 2. Use the appropriate class as your fragment's base
You can use either `PreferenceFragmentCompat` or `PreferenceFragmentCompatDividers`. The former is the fixed version of the original fragment while the latter is an extended one where you can set the dividers using the divider flags.

#### Option 1 - `PreferenceFragmentCompat`
```java
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

public class MyPreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
	
	// additional setup
    }
}
```
> **Warning!** Watch out for the correct package name when importing `PreferenceFragmentCompat`, it should come from `com.takisoft.fix.support.v7.preference`.
---
#### Option 2 - `PreferenceFragmentCompatDividers`
```java
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers;

public class MyPreferenceFragment extends PreferenceFragmentCompatDividers {

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

	// additional setup
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            return super.onCreateView(inflater, container, savedInstanceState);
        } finally {
            setDividerPreferences(DIVIDER_PADDING_CHILD | DIVIDER_CATEGORY_AFTER_LAST | DIVIDER_CATEGORY_BETWEEN);
        }
    }
}
```

### 3. Use the appropriate theme
You should set your containing `Activity`'s theme to either a variant of `@style/PreferenceFixTheme` or create your own and use it as the parent theme. `PreferenceFixTheme` is based on `Theme.AppCompat` and contains the required attribute `preferenceTheme`. The fix theme is available for all `Theme.AppCompat` variants, such as `NoActionBar`, `Light`, etc.
For example, the sample app uses `PreferenceFixTheme.Light.NoActionBar` as the parent theme:
```xml
<style name="Theme.MyTheme" parent="@style/PreferenceFixTheme.Light.NoActionBar">
    <item name="colorAccent">@color/accent</item>
    <item name="colorPrimary">@color/primary</item>
    <item name="colorPrimaryDark">@color/primary_dark</item>
    <!-- [...] -->
</style>
```

### 4. That's it!
Now you can enjoy using the support preferences API without losing all your hair.

---

## Custom solutions
### Dividers
If you use `PreferenceFragmentCompatDividers` as your base class for the preference fragment, you can use 3 new methods to customize the dividers:
- `setDivider(Drawable drawable)`: Sets a custom `Drawable` as the divider.
- `setDividerHeight(int height)`: Sets the height of the drawable; useful for XML resources.
- `setDividerPreferences(int flags)`: Sets where the dividers should appear. Check the documentation of the method for more details about the available flags.

### Hijacked `EditTextPreference`
The support implementation of `EditTextPreference` ignores many of the basic yet very important attributes as it doesn't forward them to the underlying `EditText` widget. In my opinion this is a result of some twisted thinking which would require someone to create custom dialog layouts for some simple tasks, like showing a numbers-only dialog. This is the main reason why the `EditTextPreference` gets hijacked by this lib: it replaces certain aspects of the original class in order to forward all the XML attributes set (such as `inputType`) on the `EditTextPreference` to the `EditText`, and also provides a `getEditText()` method so it can be manipulated directly.

The sample app shows an example of setting (via XML) and querying (programmatically) the input type of the `EditTextPreference`:
```xml
<EditTextPreference
    android:inputType="phone"
    android:key="edit_text_test" />
```

```java
EditTextPreference etPref = (EditTextPreference) findPreference("edit_text_test");
if (etPref != null) {
    int inputType = etPref.getEditText().getInputType();
    // do something with inputType
}
```
> **Note!** Watch out for the correct package name when importing `EditTextPreference`, it should come from `com.takisoft.fix.support.v7.preference`. If you import from the wrong package (i.e. `android.support.v7.preference`), the `getEditText()` method will not be available, however, the XML attributes will still be forwarded and processed by the `EditText`.

## Version
The current version is **26.0.0.0-beta2**.

## Notes #
This demo / bugfix is set to work on API level 14+.

### Changelog

**2017-06-14**

New version: 26.0.0.0-beta2 (based on v26.0.0-beta2)

- Support preferences v7 has some new interesting features, such as [icon space reservation](https://developer.android.com/reference/android/support/v7/preference/Preference.html#attr_android:iconSpaceReserved), [single line titles](https://developer.android.com/reference/android/support/v7/preference/Preference.html#attr_android:singleLineTitle), and the [`PreferenceDataStore`](https://developer.android.com/reference/android/support/v7/preference/PreferenceDataStore.html) interface. For details, check out the [25.4.0 -> 26.0.0-beta2 diff specification](https://developer.android.com/sdk/support_api_diff/26.0.0-beta2/changes/pkg_android.support.v7.preference.html).

> For older changelogs, check out the [CHANGELOG](CHANGELOG.md) file.

Feel free to ask / suggest anything on this page by creating a ticket (*issues*)!

# License notes #
You can do whatever you want except where noted.
