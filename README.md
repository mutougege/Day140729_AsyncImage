Day140729_AsyncImage
====================
Android 图片缓存机制Day140729_AsyncImage 项目模拟的过程是主Acitvity异步像网络请求数据（使用handler message方法）----服务器将数据返回，Activity将数据放入ListView中显示----ListView的Adapter在显示每条数据时根据图片url使用异步（AsyncTask方法）+缓存的方式下载图片并显示。
异步（AsyncTask方法）+缓存下载图片的方式比较简单： 根据url去本地找有没有对应的cache文件，如果有直接返回文件，没有就去网上下载图片，然后返回，并把图片缓存。
