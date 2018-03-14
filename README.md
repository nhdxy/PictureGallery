# PictureGallery
这是一款android上的图片选择器，分为单选和多选，单选包含裁剪功能，项目是kotlin开发
------
### 一、上图
![](https://github.com/nhdxy/PictureGallery/blob/master/screenshot/ezgif-5-9ff0898986.gif)
### 二、项目中集成
##### 1、单选不含裁剪
```Java
Anhdxy
    .from(this) //传入当前上下文
    .setCrop(false) //设置是否裁剪，不传默认是选择裁剪
    .setSelectType(SelectType.SINGLE) //设置选择方式，默认是多选
    .choose() //开启图片选择
```
##### 2、单选含裁剪
在上面基础上设置为true或者不设置
##### 3、多选图片
```Java
Anhdxy
    .from(this)
    .setMaxSelectedSize(9) //设置最多选择数量,默认为9张
    .setSelectType(SelectType.MULTI)
    .choose()
```
