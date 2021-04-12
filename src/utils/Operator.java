package utils;

/**
 * Copyright 2020 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

//package pt.up.fe.specs.comp.ex2;

public enum Operator {

    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/");

    private final String op;

    private Operator(String op) {
        this.op = op;
    }

    public String getOp() {
        return op;
    }
}
