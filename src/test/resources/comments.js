
// comment 1
/**
 * comment 2
 */
function MyFunc() {
    // comment 3
    console.log("foo");

    //comment 4
}


MyFunc("arg", {

    // comment 5
    myProp: 12345,

    /**
     * comment 6
     */
    myMethod: function(arg /* comment 7 */ ) {
        var a = 1;
    }

});

// comment 8

new MyFunc();

// comment 9