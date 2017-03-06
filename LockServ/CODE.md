Lock Server with Mutual Exclusion
=================================

Adapted from page 3 in the [Verdi paper](http://verdi.uwplse.org/verdi.pdf).

Actors
-----

```coq
Name := Server | Agent(int)
```

API
---

```coq
Input := Lock | Unlock
Out := Granted
```

Internal Messages
----------------

```coq
Msg := LockMsg | UnlockMsg | GrantedMsg
```

State
-----

```coq
State Server := list Name (* agent at head holds lock, tail agents wait *)
State (Agent _) := bool (* true iff client holds lock *)

InitState Server := []
InitState (Agent _) := false
```

API Handlers
------------

```coq
HandleInp (n: Name) (s: State n) (inp: Inp) :=
match n with
| Server => nop (* server performs no external IO *)
| Agent _ => 
  match inp with
  | Lock => 
    send (Server, LockMsg)
  | Unlock =>
    if s == true then s := false ; send (Server, UnlockMsg)
```

Internal Message Handlers
-------------------------

```coq
HandleMsg (n: Name) (s: State n) (src: Name) (msg: Msg) :=
match n with
| Server =>
  match msg with
  | LockMsg => 
    (* if lock not held, immediately grant *)
    if s == [] then send (src, GrantedMsg) ;
    (* add requestor to end of queue *)
    s := s ++ [src]
  | UnlockMsg =>
    (* head of queue no longer holds lock *)
    s := tail s ;
    (* grant lock to next waiting agent, if any *)
    if s != [] then send (head s, GrantedMsg)
  | _ => nop (* never happens *)
| Agent _ => 
  match msg with
  | GrantedMsg =>
    s := true ;
    output Granted
  | _ => nop (* never happens *)
```
