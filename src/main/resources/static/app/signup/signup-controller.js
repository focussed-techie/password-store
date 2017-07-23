(function (){
    var singupModule = angular.module("signUpModule");
    singupModule.controller("signUpController",signUpController);

    function signUpController($scope,$http,$location,alertService,rsaService){
        var ctrl = this;
        ctrl.username = '';
        ctrl.password = '';
        ctrl.confirmPassword = '';
        ctrl.message = '';



        $scope.signup = signup;
        $scope.match=match;
        $scope.doesUserExist=doesUserExist;

        function doesUserExist(value){
          console.log("calling service to verify");
           return $http.get("/usernameexists/"+value);

        }

        function match(value){
            return value ==ctrl.password;
        }


        function signup(){
            var encodedPassword = rsaService.encryptData(ctrl.password);
            var userObject = {'username' : ctrl.username, 'password' : encodedPassword};

            $http.post('/signup',userObject)
                .then(function (response)
                {
                    alertService.addAlert({type:'success', message : 'Your account is created!!!'});
                    $location.path('/login');
                }).catch(function(response){
                    alertService.addAlert({type:'warning', message : 'There was some error creating your account. Sorry about that..'});

                });


        }

    }


})();