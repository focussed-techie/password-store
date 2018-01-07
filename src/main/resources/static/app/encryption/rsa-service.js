(function (){
    var rsaModule = angular.module("rsaModule");
    rsaModule.service('rsaService',rsaService);



    function rsaService($http,$q,alertService){

        var service =this;
        service.rsa=new RSAKey();
        service.publicKey;
        service.deferred  = $q.defer();

        service.iterationCount = 1000;
        service.keySize = 128 / 32;
        service.iv = CryptoJS.lib.WordArray.random(8).toString(CryptoJS.enc.Hex);
        service.salt = CryptoJS.lib.WordArray.random(8).toString(CryptoJS.enc.Hex);
        service.passPhrase = CryptoJS.lib.WordArray.random(8).toString(CryptoJS.enc.Hex);
        console.log("salt :",service.salt);
        console.log("passphrase :",service.passPhrase);

        console.log("iv :",service.iv);

        getKeyModulus();

        return{
            promise : service.deferred.promise,
            'encryptWithPublicKeyData' : encryptWithPublicKeyData,
            'encryptData' : encryptData,
            'decryptData' : decryptData

        };

        function encryptWithPublicKeyData(data){
            return service.rsa.encrypt(data);

        }


        function getKeyModulus(){
            $http.get("/publicKey").then(function(response){
                service.publicKey=response.data;
                console.log("public key is :",service.publicKey);
                service.rsa.setPublic(service.publicKey, '10001');
                sendKeys();
            },function (response) {

            });
        }

       function decryptData(data){
            var key = getSymetrickey();
            var cipherParams = CryptoJS.lib.CipherParams.create({
                ciphertext: CryptoJS.enc.Base64.parse(data)
            });
            var decrypted = CryptoJS.AES.decrypt(
                cipherParams,
                key,
                { iv: CryptoJS.enc.Hex.parse(service.iv) });
            return decrypted.toString(CryptoJS.enc.Utf8);

        };

       function encryptData(data){
            var encrypted = CryptoJS.AES.encrypt(
                data,
                getSymetrickey(),
                { iv: CryptoJS.enc.Hex.parse(service.iv) });
            return encrypted.ciphertext.toString(CryptoJS.enc.Base64);

        };


        function getSymetrickey() {
            var key = CryptoJS.PBKDF2(
                service.passPhrase,
                CryptoJS.enc.Hex.parse(service.salt),
                {keySize: service.keySize, iterations: service.iterationCount});
            return key;
        }

        this.getPassPhrase = function(){
            return service.passPhrase;
        }

        this.getsalt = function(){
            return service.salt;
        }

        this.getIv = function(){
            return service.iv;
        }

        this.getKey = function(){
            return service.symetricKey;
        }

        function sendKeys(){
            var salt = encryptWithPublicKeyData(service.salt);
            var passPhrase = encryptWithPublicKeyData(service.passPhrase);
            var iv = encryptWithPublicKeyData(service.iv);
            var keys = {'salt' : salt,
                'passPhrase' : passPhrase,
                'iv' : iv
            };

            $http.post("/saveKeys",keys).then(function(response){
                service.deferred.resolve(response);
            },function (res) {
                alertService.addAlert(res.data);
                service.deferred.reject(res);
            });
        }




    }




})();