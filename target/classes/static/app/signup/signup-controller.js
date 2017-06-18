(function (){
    var singupModule = angular.module("signUpModule");
    singupModule.controller("signUpController",signUpController);

    function signUpController($scope,$http,$location){
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
            var userObject = {'username' : ctrl.username, 'password' : ctrl.password};

            $http.post('/signup',userObject)
                .then(function (response)
                {
                    ctrl.message = "Your account has been created";
                    $location.path('/login');
                },function(response){
                    ctrl.message = "There was some error creating your account. Sorry about that...";
                });


        }

    }


})();