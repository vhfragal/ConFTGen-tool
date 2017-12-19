(define-sort Feature () Bool)
(declare-const R Feature)
(declare-const G Feature)
(declare-const A Feature)
(declare-const B Feature)
(declare-const C Feature)

(assert R)
(assert (and
   (= G R)
   (= (or A  B ) G)
   (not (and A B))
   (=> C R)
))


 (assert (and R G A (not B) (not C) ))(push)
(assert A)
(assert (and 
))
(check-sat)
(pop)
