(function (){
    var singupModule = angular.module("signUpModule");
    singupModule.controller("signUpController",signUpController);

    function signUpController($scope,$http,$location, $routeParams){
        var ctrl = this;
        ctrl.username = '';
        ctrl.password = '';
        ctrl.message = '';



        $scope.signup = signup;



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