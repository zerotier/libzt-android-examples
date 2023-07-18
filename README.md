# libzt Android Examples

This repo has several examples of using libzt in Android clients.


## Examples

### exoplayerclient

Stream video with ExoPlayer using libzt.

A `ZeroTierSocketsSocketFactory` class is included.

This example assumes that you have built the libzt AAR locally.

See [here](https://github.com/zerotier/libzt/blob/main/README.md) for instructions on building the libzt AAR locally.

You must create a zerotier.properties file at the root of the project with this content:
```
libzt.aar=/path/to/libzt.aar
```

NOTE: This is demo code and waiting on main thread is not recommended.

For the server:
```
sudo apt install vlc
```
```
vlc mst3k.mp4 --loop --sout="#std{access=http, mux=ts, dst=:8090/sample}"
```


### httpurlconnectionclient

Use `HttpURLConnection` to talk to an HTTP server using libzt.

This example assumes that you have built the libzt AAR locally.

See [here](https://github.com/zerotier/libzt/blob/main/README.md) for instructions on building the libzt AAR locally.

You must create a zerotier.properties file at the root of the project with this content:
```
libzt.aar=/path/to/libzt.aar
```

NOTE: This is demo code and waiting on main thread is not recommended


### okhttpclient

Use `OkHttp` to talk to an HTTP server using libzt.

A `ZeroTierSocketsSocketFactory` class is included.

This example assumes that you have built the libzt AAR locally.

See [here](https://github.com/zerotier/libzt/blob/main/README.md) for instructions on building the libzt AAR locally.

You must create a zerotier.properties file at the root of the project with this content:
```
libzt.aar=/path/to/libzt.aar
```

NOTE: This is demo code and waiting on main thread is not recommended


## Questions

I get this error when building:
```
A problem occurred evaluating project ':app'.
> wd-android-poc/exoplayerclient/zerotier.properties (No such file or directory)
```

You must create a zerotier.properties file at the root of the project with this content:
```
libzt.aar=/path/to/libzt.aar
```


I get this error when building:
```
error: cannot find symbol
import com.zerotier.sockets.ZeroTierNative;
```

The path to the libzt AAR in zerotier.properties is not correct.





