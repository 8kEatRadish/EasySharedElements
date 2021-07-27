# EasyShareElement(一个适用于Android全版本的共享元素动画)

使用apt实现页面跳转共享动画，共享动画可以分别设置进入动画时间、退出动画时间、进入动画插值器、退出动画的插值器、进入动画的优先级、退出动画的优先级。多个共享动画，同级别会并行执行，不同级别会串行执行。
### **TODO**
- [x] ~~实现Activity跳转参数标识，共享元素标识;~~
- [x] ~~多个共享元素执行动画~~
- [x] ~~跳转动画可自定义实现~~
- [x] ~~添加动画执行状态回调~~
- [x] ~~设置动画串并行逻辑~~

# 效果展示
![apt01.gif](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt01.gif)
- **上图的共享元素属性**
![apt03.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt03.png)

* * *

![apt02.gif](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt02.gif)

- **上图的共享元素属性**

![apt04.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt04.png)


# 需要掌握的知识
## 什么是apt
**APT(Annotation Processing Tool)** 是javac的一个工具，可以在编译期间扫描和处理注解(Annotation)，获得注解和被注解的对象的相关信息，这些信息可以用于生成符合业务的代码，不需要手动编写一些业务代码，提升了开发效率。这些事情都是编译器就做的，对比传统反射方式在运行时做操作来说，apt提升了程序的性能。
## 共享动画
> Material Design 应用中的 Activity 过渡通过常见元素之间的动画和转换效果，在不同状态之间建立视觉联系。您可以为进入和退出过渡，以及 Activity 之间共享元素的过渡指定自定义动画。
**共享动画**是适用于Android5.0及更高版本的Activity的过渡共享元素动画，可以定义两个Activity中不同的View使用一个共同的android:transitionName，在页面跳转是会有视觉联系。[**Android For Developers**](https://developer.android.com/training/transitions/start-activity?hl=zh-cn#CheckVersion)
# 实现步骤

**首先我们看一下项目模块：**


![image.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt05.png)

- **annotations**:存放注解的java模块
- **compiler**:我们的注解处理器模块，在编译过程中，会执行我们相应的注解处理，生成符合业务需求的java或者kotlin代码。
- **runtime**:定义一些工具类处理Android动画相关，页面跳转相关。
- **app**:测试模块

## 编写annotations模块
我们第一步要编写我们的注解，定义我们需要的信息，在这里我们需要Activity相关信息，跳转Activity时Intent相关信息，还有就是共享元素的相关信息。

> 编写注解的时候我们会用到两个注解，一个是@Target，另一个是@Retention。其中@Target标明的是注解的目标，@Retention表明的是注解的保留位置。

### @Target参数含义
- **ElementType.TYPE**:用于类或接口上;
- **ElementType.FIELD**:用于类的成员变量上;
- **ElementType.METHOD**:用于方法上;
- **ElementType.PARAMETER**:用于方法参数上;
- **ElementType.CONSTRUCTOR**:用于构造方法上;
- **ElementType.LOCAL_VARIABLE**:用于局部变量上;
- **ElementType.ANNOTATION_TYPE**:用于注解上;
- **ElementType.PACKAGE**:用于记录java文件的package信息;
- **ElementType.TYPE_PARAMETER**:用于标注泛型的类型参数;
- **ElementType.TYPE_USE**:标注各种类型;

### @Retention参数含义

- **RetentionPolicy.SOURCE**:保留在原文件，不会生成class字节码;
- **RetentionPolicy.CLASS**:保留在class文件中，运行时无法获得;
- **RetentionPolicy.RUNTIME**:保留到运行时，运行时可以获得;


### 构建Annotation
- 我们希望处理器只处理我们标注的Activity，所以定义的Builder.class:


![apt06.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt06.png)

- 我们定义跳转必须需要的参数annotation:

![apt07.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt07.png)

- 定义跳转可选参数annotation(含有默认值):


![apt08.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt08.png)

- 定义共享元素相关信息annotation:


![apt09.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt09.png)

- 定义入场动画执行完毕回调annotation:


![apt10.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt10.png)

## 编写注解处理器

### 定义BuilderProcessor

BuilderProcessor.kt继承于AbstractProcessor实现相关方法实现对自定义接口处理。


![apt11.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt11.png)


- 其中getSupportedAnnotationTypes方法用于定义处理器需要处理什么类型的注解，也可以使用@SupportedAnnotationTypes注解来定义。

- 其中process方法用于我们收集注解及其注解目标的信息，以便后续生成代码使用。
在这里我们做的逻辑是：
1. 收集所有标注Builder注解的Activity类信息到一个Map。
2. 分别收集@Required、@Optional、@SharedElement、@RunEnterAnim注解的信息，加入到Map中相应Builder标注的Activity信息当中。
3. 遍历Map，使用Activity的信息生成相应Builder类用于辅助调用。

### Entity类

#### ActivityClass

![apt12.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt12.png)

- 其中fields存放相关成员变量信息(必要参数、可选参数、共享元素信息)。
- startMethodName存放动画开始回调方法名字。
- endMethodName存放动画结束回调方法名字。
#### Field


![apt13.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt13.png)

这里记录成员变量的基本信息，实现Comparable接口来排序。

#### OptionalField

![apt14.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt14.png)

OptionalField继承于Field类，重写了前缀名，重新了排序逻辑，只和OptionalField进行排序，其他让行，增加了默认值。

#### SharedElementField

![apt16.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt16.png)

SharedElementField用于共享元素的信息记录。

## 编写runtima模块

该模块定义一些工具类处理Android动画相关，页面跳转相关

### 定义View信息类

要把View的信息通过Intent传递，需要进行序列化，所以我们要实现Parcelable接口，在View信息类当中要记录上一个activity中View的信息和跳转activity中View的id，还有执行动画的一些参数也要在这里传递过去，所以ViewAttrs类如下：

![apt15.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt15.png)

### 状态保存实现

为了实现状态保存，我们要拿到Application，然后registerActivityLifecycleCallbacks，来监听Activity的生命周期，在onActivitySaveInstanceState回调中保存当前页面数据，在onActivityCreated回调中重新加载数据。在ActivityBuilder类中定义了startActivity方法，判断上下文是否为Activity，如果不是的话，需要给Intent添加Intent.FLAG_ACTIVITY_NEW_TASK。

![apt17.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt17.png)

### 共享动画优先级执行实现

共享动画优先级执行逻辑的实现是通过优先队列(PriorityQueue)对所以共享元素进行排序，按照同级并行，不同级串行的逻辑执行动画。记录每一级所需要的最大时间，求出最大总时间，延迟发送动画完成回调。

**注意**:在入场动画进行时，执行退场动画会出现View乱动的问题，这是因为我们动画使用的是View.animation()方法设置的属性动画，如果在一个View上面同时执行两个动画，会出现属性值变化错乱。
在这里我加了一个volatile属性来控制在执行入场动画的时候不可以退出，来防止这个问题出现。


![apt18.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt18.png)

## 使用javapoet、kotlinpoet生成代码

javapoet是JakeWharton大神开发的一个框架，是一个可以让我们更加方便的生成java源文件的框架，不需要在用StringBuilder一直append()个不停。而kotlinpoet则是javapoet的生成kotlin源文件版本。在apt中我们使用这两个框架生成相关代码。

### ActivityClassBuilder


![apt19.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt19.png)

这个类用来生成各个标注@Buidler的ActivityNameBuilder.java类，在这里TypeSpec.classBuilder(name)用来创建java类，addModifiers用来定义类的修饰，使用ConstantBuilder类来生成类的成员变量，使用startMethodBuilder生成start方法，使用SaveStateMethodBuilder生成保存状态方法，InjectMethodBuilder生成注入方法，RunExitAnimMethodBuilder生成退出动画方法，最后调用build()方法构建完成类，然后写入文件。

### InjectMethodBuilder

在这里我们只例举一个方法生成来具体说明如何生成方法的。

![apt20.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt20.png)

- MethodSpec.methodBuilder(name)来创建一个名字为name的方法；
- addParameter（type，name）是添加一个参数，type为类型，name为参数名字；
- addModifiers是添加方法修饰
- addStatement是添加具体代码，\$定义变量，如果后面跟着的是T，则代表变量是类，是L则是把本身带入，是S则是代表需要带入字符串。
- beginControlFlow条件控制语句需要用此方法添加，不要忘记在条件控制之行结束的时候调用endControlFlow，每一个beginControlFlow都要对应调用endControlFlow。

# 测试app模块引入使用
- 在app测试模块的build.gradle文件中引入相关模块

![apt21.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt21.png)
- 然后在需要使用共享元素的Activity类中使用我们的注解

![apt23.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt23.png)
- 在build项目之后，会在/app/build/generated/source/kapt/debug(release)中找到我们自动生成的DetailsActivityBuilder
![apt22.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt22.png)


![apt24.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt24.png)

- 在mainActivity中调用生成的代码进行跳转

![apt25.png](https://github.com/8kEatRadish/EasySharedElements/blob/master/images/apt25.png)


# 彩蛋

> **用户无感知注入框架**

在使用leakcanary的时候，发现这个框架只需要引入就可以使用，很是好奇他是怎么做到的。拜读了源码之后发现是使用ContentProvider来实现自动化注入的，具体方法如下：
- 在AndroidManifest.xml中注册provider
``` xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.sniperking.eatradish.runtime">

    <application>
        <provider
            android:authorities="${applicationId}.ese"
            android:name="com.sniperking.runtime.InstallRunTime"
            android:enabled="true"
            android:multiprocess="true"
            android:exported="false"/>
    </application>
</manifest>
```

- 编写ContentProvider

``` kotlin
class InstallRunTime : ContentProvider() {
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun onCreate(): Boolean {
        val application = context!!.applicationContext as Application
        ActivityBuilder.INSTANCE.init(application)
        return true
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }
}
```

在这里其他方法都不做处理，只处理onCreate()方法，来做框架的初始化，这样就可以做到用户无感知注入框架，很方便。
