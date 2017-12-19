(define-sort Feature () Bool)
(declare-const R Feature)
(declare-const G Feature)
(declare-const A Feature)
(declare-const B Feature)
(declare-const C Feature)
(declare-const D Feature)

(assert R)
(assert (and
   (= G R)
   (= (or A  B ) G)
   (not (and A B))
   (=> C R)
   (=> D R)
))

(push)
(assert B)
(assert (and 
))
(check-sat)
(pop)
