package {
public class Sub extends Base {
  public override function b<caret>ar() {}
}

public class Base extends Super {
  public override function bar() {}
}

public class Super implements IFoo {
  public function bar() {}
}

public interface IFoo {
  function bar();
}

public class Ref {
  function ref() {
    var t : IFoo;
    t.bar();
  }

}
}
