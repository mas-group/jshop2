; This extremely simple example shows some of the most essential
; features of SHOP2.

(defdomain basic (
  (:operator (!pickup ?a) () () ((have ?a)))
  (:operator (!drop ?a) ((have ?a)) ((have ?a)) ())

  (:method (swap ?x ?y)
    ((have ?x) (not (have ?y)))
    ((!drop ?x) (!pickup ?y))
    ((have ?y) (not (have ?x)))
    ((!drop ?y) (!pickup ?x)))))
