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
(assert (and (and A (not C)) (and A C)))
(check-sat)
(pop)

(push)
(assert (and A (not C)))
(assert (and (not (and A C))))
(check-sat)
(pop)

