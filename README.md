# kubesecret (poc)

## what?
- poc: compile kotlin (jvm) to graalvm native-image (on osx)
- artifact: a cli application to read / decode / encode k8s secrets - compiled as executable binary

- why? research + playground ;)

## download release (osx)

- here: https://github.com/bastman/kubesecret/tree/master/release

```
# download release from github (osx, wget)
$ wget https://github.com/bastman/kubesecret/raw/master/release/kubesecret.darwin-amd64 && chmod +x ./kubesecret.darwin-amd64
```

## quick start
```
$ kubesecret --help
```
![Alt text](docs/kubesecret_help.png?raw=true "screenshot")

```
$ kubesecret list
```
![Alt text](docs/kubesecret_list.png?raw=true "screenshot")

```
$ kubesecret get example-secret
```
![Alt text](docs/kubesecret_get.png?raw=true "screenshot")

```
$ kubesecret get example-secret --base64-decode
```
![Alt text](docs/kubesecret_get_and_decode.png?raw=true "screenshot")



## cheat sheet 

```
# help
$ kubesecret --help

# list secrets (uses kubectl)
$ kubesecret list --help
$ kubesecret list

# get secret (uses kubectl)
$ kubesecret get --help
$ kubesecret get <SECRET_NAME> --base64-decode

# base64-decode secret
$ kubesecret base64-decode --help
$ cat example-secrets/secret.yaml | kubesecret base64-decode
$ kubesecret get <SECRET_NAME> | kubesecret base64-decode

# base64-encode secret
$ kubesecret base64-encode --help
$ cat example-secrets/secret-plain.yaml | kubesecret base64-encode

```



### graalvm: docs
- https://medium.com/graalvm/understanding-class-initialization-in-graalvm-native-image-generation-d765b7e4d6ed
- http://royvanrijn.com/blog/2018/09/part-1-java-to-native-using-graalvm/

### graalvm: install
- get sdkman: https://sdkman.io/

```
 $ sdk list java
 $ sdk install java 1.0.0-rc6-graal
 $ sdk use java 1.0.0-rc6-graal
```