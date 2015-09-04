# The issue has been fixed, this project should not be used anymore as a workaround!
**According to the latest support library, revision 23.0.1**

> Changes for v7 and v14 Preference Support library:

> - Added the material design layout and style files. ([Issue 183376](https://code.google.com/p/android/issues/detail?id=183376))

# Android-Support-Preference-V7-Fix
Android preference-v7 support library doesn't contain material design layout files so the preferences screen look bad on API 21+. This is a temporary fix until Google fixes it.

The issue has been reported, you can find it here:
https://code.google.com/p/android/issues/detail?id=183376

# Prerequisites #
This demo / bugfix is set to work on API level 16+.

# License notes #
You can do whatever you want except where noted, especially the following files downloaded from the [Android framework base](https://github.com/android/platform_frameworks_base/tree/master/core/res/res/layout) (these are also modified):
 - preference_category_material.xml
 - preference_dialog_edittext_material.xml
 - preference_information_material.xml
 - preference_material.xml
