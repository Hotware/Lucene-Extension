package com.github.hotware.lucene.extension.hsearch.util.reflect;

import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;

import junit.framework.TestCase;

public class ReflectionManagerTryOut extends TestCase {
	
	public static class A {
		
		private B b;
		
		public void setB(B b) {
			this.b = b;
		}
		
		public B getB() {
			return this.b;
		}
		
	}
	
	public static class B {
		
		private int c;
		
		public void setC(int c) {
			this.c = c;
		}
		
		public int getC() {
			return this.c;
		}
		
	}
	
	public void test() {
		ReflectionManager reflect = new JavaReflectionManager();
		XClass xClazz = reflect.classForName(A.class.getName());
		xClazz.getDeclaredProperties(XClass.ACCESS_PROPERTY);
		
	}

}
