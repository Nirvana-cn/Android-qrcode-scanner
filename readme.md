## Android-QRCode-Scanner

引用请把`qrcode`模块复制到主工程目录下。

- VideoStreamManager类负责视频流管理
- QRCodeRecognition类负责识别二维码内容
- ScannerManager类是对外暴露的接口

支持自定义任意识别区域。

理论上来说`qrcode`模块只提供相机视频流和二维码识别功能，不提供上层交互界面。

工程依赖：

- CameraX
- zxing

