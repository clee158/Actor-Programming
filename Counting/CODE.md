Counting Actors Through Tree-Based Aggregation
==============================================

Actors
-----

```coq
Name := Root | NonRoot(int)
```

API
---

```coq
Inp := SendAggregate | Broadcast | AggregateRequest | LevelRequest | Stop
Out := AggregateResponse(int) | LevelResponse(option int)
```

Internal Messages
----------------

```coq
Msg ::= Aggregate(int) | Level(option int) | Stopped
```

State
-----

```coq
State Root := { aggregate : int ; adjacent : Set.t ; balance : Map.t int }
State (NonRoot _) := { aggregate : int ; adjacent : Set.t ; balance : Map.t int ; levels : Map.t int }

InitState Root (neighbors : Set.t) := { 1 ; neighbors ; Map.empty int }
InitState (NonRoot _) (neighbors : Set.t) := { 1 ; neighbors ; Map.empty int ; Map.empty int }
```

API Handlers
------------

```coq
HandleInp (n : Name) (s : State n) (i : Inp) :=
match n with
| Root =>
  match i with
  | SendAggregate => nop
  | AggregateRequest =>
    output (AggregateResponse s.aggregate)
  | Broadcast => nop  
  | LevelRequest => 
    output (LevelResponse (Some 0))
  | Stop => nop (* root never terminates *)
| Nonroot _ =>
  match i with
  | SendAggregate =>
    if s.aggregate != 0 then
      match parent s.adjacent s.levels with
      | None => nop
      | Some dst =>
        send (dst, Aggregate s.aggregate) ;
        s.aggregate := 0
  | AggregateRequest =>
    output (AggrgateResponse s.aggregate)
  | Broadcast =>
    foreach dst in s.adjacent send (dst, Level (level s.adjacent s.levels))
  | LevelRequest =>
    output (level s.adjacent s.levels)
  | Stop =>
    foreach dst in s.adjacent send (dst, Stopped) ;
    halt
```

Internal Message Handlers
-------------------------

```coq
HandleMsg (n: Name) (s: State n) (src: Name) (msg: Msg) :=
match n with
| Root =>
  match msg with 
  | Aggregate a =>
    match Map.get s.balance src with
    | None => nop (* never happens *)
    | Some a' =>
      s.aggregate := s.aggregate + a
      s.balance := Map.add s.balance src (a' - a)
  | Level _ => nop
  | Stopped =>
    match Map.get s.balance src with
    | None => nop (* never happens *)
    | Some a =>
      s.aggregate := s.aggregate + a
      s.adjacent := Set.remove s.adjacent src
| NonRoot _ =>
  match msg with 
  | Aggregate a =>
    match Map.get s.balance src with
    | None => nop (* never happens *)
    | Some a' =>
      s.aggregate := s.aggregate + a
      s.balance := Map.add s.balance src (a' - a)
  | Level None =>
    if level s.adjacent s.levels != level s.adjacent (Map.remove s.levels src) then s.broadcast := true ;
    s.levels := Map.remove s.levels src
  | Level (Some lv) =>
    if level s.adjacent s.levels != level s.adjacent (Map.add s.levels src lv) then s.broadcast := true ;
    s.levels := Map.add s.levels src lv
  | Stopped =>
    match Map.get s.balance src with
    | None => nop (* never happens *)
    | Some a' =>
      if level s.adjacent s.levels == level (Set.remove s.adjacent src) (Map.remove s.levels src) then s.broadcast := true ;
      s.aggregate := s.aggregate + a' ;
      s.levels := Map.remove s.levels src ;
      s.adjacent := Set.remove s.adjacent src ;
      s.balance := Map.remove s.balance src
```
