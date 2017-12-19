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

(assert (and (and (and A (and A A)) A (and A A)) (and (and A (and A A)) A (and A A))))
(assert (and 
    (not (and true (and (and A (and A A)) A (and A A)) true (and (and A (and A A)) A (and A A)) ))
))
(check-sat)