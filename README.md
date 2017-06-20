# CustomProgress
一个常见的进度条显示框架

![example](gif/loadingView.gif)

- 直线
LineProgressBar

- 圆形
ArcProgress

- 扇形
SectorProgress

如果你想在项目中引进次框架

请在你的项目build.gradle中添加以下依赖即可

- 第一步

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

- 第二步

```
dependencies {
	        compile 'com.github.itgoyo:CustomProgress:v1.0'
	}

```