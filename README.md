# DialogHelper

Lightweight Android library that simplifies management of DialogFragments.

## Install

To use DialogHelper in your project, make sure that you have Maven Central set up in your root `build.gradle` script:

```
allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
```

Then add DialogHelper as a dependency into your main module's (called `app` by default) `build.gradle` script:

```
dependencies {
    implementation 'com.techyourchance:dialoghelper:1.0.0'
}
```


## Usage

Show an instance of MyDialogFragment (and dismiss currently shown dialog, if exists):

```java
mDialogHelper.showDialog(MyDialogFragment.newInstance(), null);
```

Dialogs can be given IDs. That's handy if you'll need to distinguish between mutliple dialogs at a later point in time. To show an instance of MyDialogFragment with a specific ID, just pass the ID as the second argument:

```java
mDialogHelper.showDialog(MyDialogFragment.newInstance(), DIALOG_ID);
```

Then, if you need the ID of the currently shown dialog, just do:

```java
mDialogHelper.getCurrentlyShownDialogId()
```

If you want to get dialog's ID from within the dialog itself, you'll need to use an instance of `DialogHelper` inside that dialog:

```java
mDialogHelper.getDialogId(this)
``` 

## Features

DialogHelper is "lifecycle safe". This means that if you do:

```java
mDialogHelper.showDialog(MyProgressDialogFragment.newInstance(), DIALOG_ID);
```

and then your application undergoes either configuration change or full save & restore (i.e. process death), this check will succeed afterwards:

```java
@Override
protected void onStart() {
    super.onStart();
    if (DIALOG_ID.equals(mDialogHelper.getCurrentlyShownDialogId())) {
        // check the status of the flow and dismiss the dialog if the flow has already completed
    }
}
```

You can make DialogHelper a global singleton, but you don't have to. It will function properly even if you'll create dedicated instances in each Activity, Fragment, etc.:

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mDialogHelper = new DialogHelper(getSupportFragmentManager()); // Activity-specific instance
}
```

## Important note

Due to its internal implementation details, DialogHelper will function properly only if it manages all application's dialogs (i.e. you show all the dialogs using DialogHelper). Mixing DialogHelper with other approaches might result in partial loss of functionality.

## Sample application

To get deeper insights into DialogHelper's role, you can review [sample application's source code](https://github.com/techyourchance/dialog-helper/tree/master/sample/src/main/java/com/techyourchance/dialoghelpersample).

Except for basic DialogHelper usage, this application shows some advanced design and architectural techniques that I find very useful in real Android projects. In addition, it demonstrates workarounds for quite a few limitations and bugs that I encountered over the years. 

## License

This project is licensed under the Apache-2.0 License - see the [LICENSE.txt](LICENSE.txt) file for details

