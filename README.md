# kubesecret (POC)


![Alt text](docs/kubesecret_screenshot.png?raw=true "screenshot")

## What?
- poc: compile kotlin to graalvm native-image (on osx)
- artifact: a cli application to read k8s secrets

- why? I need a cli tool to base64 decode a k8s secret.yml


## Download Release (OSX)

- here: https://github.com/bastman/kubesecret/tree/master/release

```
# download release from github (osx, wget)
$ wget https://github.com/bastman/kubesecret/raw/master/release/kubesecret.darwin-amd64 && chmod +x ./kubesecret.darwin-amd64

```

## Usage examples 

```
# help
$ ./build/kubesecret.darwin-amd64 --help

# list secrets (uses kubctl)
$ ./build/kubesecret.darwin-amd64 list --help
$ ./build/kubesecret.darwin-amd64 list

# get secret (uses kubctl)
$ ./build/kubesecret.darwin-amd64 get --help
$ ./build/kubesecret.darwin-amd64 get <SECRET_NAME>

# base64-decode secret

$ ./build/kubesecret.darwin-amd64 base64-decode --help
$ cat /some-project/secret.yaml | ./build/kubesecret.darwin-amd64 base64-decode
$ ./build/kubesecret.darwin-amd64 get <SECRET_NAME> | ./build/kubesecret.darwin-amd64 base64-decode
```


### graalvm docs
- https://medium.com/graalvm/understanding-class-initialization-in-graalvm-native-image-generation-d765b7e4d6ed