package {

}

class U {
  private var foo:String;
}

class B extends U {
  public function zz():void {
    var v : U;
    v.<error>foo</error> = "a";
  }
}
