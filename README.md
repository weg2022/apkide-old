# APK-IDE

APK-IDE是一款用于反编译；编译；逆向；修改APK的安卓软件。
编译与反编译核心使用的是 APK-Tool 开源项目

## 功能 （待实现）

- [ ] UI
  - [ ] 外观
    - [x] 夜间主题支持
    - [x] 跟随系统主题
  - [ ] 浏览器
    - [ ] 文件浏览器
      - [x] 支持 Jar 文件访问
      - [x] 支持 class 文件/Jar 中的 class 文件访问（转换为 Java 源代码）
      - [x] 支持 图像 文件预览（JPG,PNG,GIF,WEBP)
      - [x] 支持 与磁盘同步
      - [ ] 支持 复制 文件名/文件路径
      - [x] 支持 新建 文件/目录
      - [x] 支持 删除 文件/目录
      - [x] 支持 重命名 文件/目录
      - [x] 支持 反编译 Apk 文件
    - [ ] 项目浏览器
    - [ ] 查找浏览器
    - [ ] 问题浏览器
    - [ ] 构建浏览器
- [ ] 代码编辑器
  - [x] 快速打开与编辑  
  - [ ] 查找替换
  - [ ] 撤销与重做
  - [ ] 快捷键
  - [ ] 超链接
  - [ ] 颜色高亮
- [ ] 代码语言服务
  - [ ] Smali
    - [ ] 代码分析
      - [ ] 诊断
    - [ ] 代码重构
      - [ ] 重命名
      - [ ] 安全删除
      - [ ] 内联
      - [ ] 格式化
      - [ ] 缩进与取消缩进
      - [ ] 注释与取消注释
    - [ ] 代码补全 
      - [ ] 基本 
      - [ ] 类型
      - [ ] 代码导航
        - [ ] 查找 API
        - [ ] 查找用法
      - [ ] 代码高亮 
        - [ ] 基本
        - [ ] 语义
  - [ ] Java
    - [ ] 代码分析
      - [ ] 诊断
    - [ ] 代码重构
      - [ ] 重命名
      - [ ] 安全删除
      - [ ] 内联
      - [ ] 格式化
      - [ ] 缩进与取消缩进
      - [ ] 注释与取消注释
    - [ ] 代码补全
      - [ ] 基本
      - [ ] 类型
      - [ ] 代码导航
        - [ ] 查找 API
        - [ ] 查找用法
      - [ ] 代码高亮
        - [ ] 基本
        - [ ] 语义
  - [ ] Xml
    - [ ] 代码分析
      - [ ] 诊断
    - [ ] 代码重构
      - [ ] 重命名
      - [ ] 安全删除
      - [ ] 内联
      - [ ] 格式化
      - [ ] 缩进与取消缩进
      - [ ] 注释与取消注释
    - [ ] 代码补全
      - [ ] 基本
      - [ ] 类型
      - [ ] 代码导航
        - [ ] 查找 API
        - [ ] 查找用法
      - [ ] 代码高亮
        - [ ] 基本
        - [ ] 语义
## Screenshot

![ui1](/images/1.png)

![ui2](/images/2.png)

![ui3](/images/3.png)

![ui4](/images/4.png)

### 使用的库与资源

* [androidx](https://github.com/androidx/androidx)
* [material-components](https://github.com/material-components/material-components-android)
* [apktool](https://github.com/iBotPeaches/Apktool)
* [smali](https://github.com/google/smali)
* [guava](https://github.com/google/guava)
* [commons-text](https://commons.apache.org/proper/commons-text)
* [snakeyaml](https://bitbucket.org/snakeyaml/snakeyaml)
* [xpp3](https://github.com/codelibs/xpp3)
* [jcommander](http://jcommander.org/)
* [fernflower](https://github.com/fesh0r/fernflower)
* [r8](https://r8.googlesource.com/r8)
* [okbinder](https://github.com/7hens/okbinder)
* [antlr-runtime](https://github.com/antlr/antl3)
* [antlr4-runtime](https://github.com/antlr/antlr4)

## License

[![Github license](https://img.shields.io/github/license/weg2020/apkide)](https://github.com/weg2020/apkide/blob/main/LICENSE)


