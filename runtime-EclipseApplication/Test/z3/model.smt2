(define-sort Feature () Bool)
(declare-const R Feature)
(declare-const G Feature)
(declare-const B Feature)
(declare-const A Feature)

(assert R)
(assert (and
   (= G R)
   (= (or B  A ) G)
   (not (and B A))
))

(push)
(assert B)
(assert (and 
))
(check-sat)
(pop)
